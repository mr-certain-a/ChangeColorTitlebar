@file:Suppress("PropertyName", "NonAsciiCharacters")
package gui.jna

import com.sun.jna.Native
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser.*
import com.sun.jna.win32.W32APIOptions
import gui.jna.MyUser32.Companion.GWLP_WNDPROC
import java.awt.*

class MyWindowProc : WindowProc {
    companion object {
        private const val WM_NCCALCSIZE = 0x0083
        private const val WM_NCHITTEST = 0x0084
        private const val HTCLIENT = 1L
        private const val HTTOPLEFT = 13L
        private const val HTTOP = 12L
        private const val HTCAPTION = 2L
        private const val HTTOPRIGHT = 14L
        private const val HTLEFT = 10L
        private const val HTRIGHT = 11L
        private const val HTBOTTOMLEFT = 16L
        private const val HTBOTTOM = 15L
        private const val HTBOTTOMRIGHT = 17L

        private const val TITLE_BAR_HEIGHT = 27
        private const val CONTROL_BOX_WIDTH = 150
        private const val ICON_WIDTH = 40
        private const val MAXIMIZED_WINDOW_FRAME_THICKNESS = 10
        private const val FRAME_RESIZE_BORDER_THICKNESS = 6
    }

    private val myUser32 = Native.load("user32", MyUser32::class.java, W32APIOptions.DEFAULT_OPTIONS) as MyUser32

    private var hwnd = HWND()
    private lateinit var defWndProc: LONG_PTR

    fun init(hwnd: HWND) {
        this.hwnd = hwnd
        defWndProc = myUser32.SetWindowLongPtr(hwnd, GWLP_WNDPROC, this)

        myUser32.SetWindowPos(hwnd, hwnd, 0, 0, 0, 0,
            SWP_NOMOVE or SWP_NOSIZE or SWP_NOZORDER or SWP_FRAMECHANGED
        )

        // リサイズを可能にするため WS_THICKFRAME を追加する
        val style = myUser32.GetWindowLong(hwnd, GWL_STYLE).or(0x00040000 /* =WS_THICKFRAME*/)
        myUser32.SetWindowLong(hwnd, GWL_STYLE, style)
    }

    override fun callback(hwnd: HWND, uMsg: Int, wparam: WPARAM, lparam: LPARAM) =
        when (uMsg) {
            WM_NCCALCSIZE -> LRESULT(0)
            WM_NCHITTEST -> hitTest(hwnd)
            WM_DESTROY -> {
                myUser32.SetWindowLongPtr(hwnd, GWLP_WNDPROC, defWndProc)
                LRESULT(0)
            }
            else ->
                myUser32.CallWindowProc(defWndProc, hwnd, uMsg, wparam, lparam)
        }

    private fun hitTest(hWnd: HWND): LRESULT {
        val point = POINT()
        val rect = RECT()

        User32.INSTANCE.GetCursorPos(point)
        User32.INSTANCE.GetWindowRect(hWnd, rect)

        val rcWindow = rect.toRectangle()
        val pt = Point(point.x, point.y)

        // タイトルバー判定矩形
        val titleBarRect = Rectangle(rcWindow).apply {
            x += ICON_WIDTH
            width -= CONTROL_BOX_WIDTH + ICON_WIDTH
            height = TITLE_BAR_HEIGHT + MAXIMIZED_WINDOW_FRAME_THICKNESS
        }

        // 上方リサイズ判定矩形
        val topResizeRect = Rectangle(rcWindow).apply {
            height = FRAME_RESIZE_BORDER_THICKNESS
        }

        // 下方リサイズ判定矩形
        val bottomResizeRect = Rectangle(rcWindow).apply {
            y += height - FRAME_RESIZE_BORDER_THICKNESS
            height = FRAME_RESIZE_BORDER_THICKNESS
        }

        // 左側リサイズ判定矩形
        val leftResizeRect = Rectangle(rcWindow).apply {
            width = FRAME_RESIZE_BORDER_THICKNESS
        }

        // 右側リサイズ判定矩形
        val rightResizeRect = Rectangle(rcWindow).apply {
            x += width - FRAME_RESIZE_BORDER_THICKNESS
            width = FRAME_RESIZE_BORDER_THICKNESS
        }

        return when {
            leftResizeRect.contains(pt) ->
                when {
                    topResizeRect.contains(pt) -> LRESULT(HTTOPLEFT)
                    bottomResizeRect.contains(pt) -> LRESULT(HTBOTTOMLEFT)
                    else -> LRESULT(HTLEFT)
                }
            rightResizeRect.contains(pt) ->
                when {
                    topResizeRect.contains(pt) -> LRESULT(HTTOPRIGHT)
                    bottomResizeRect.contains(pt) -> LRESULT(HTBOTTOMRIGHT)
                    else -> LRESULT(HTRIGHT)
                }
            topResizeRect.contains(pt) -> LRESULT(HTTOP)
            bottomResizeRect.contains(pt) -> LRESULT(HTBOTTOM)
            titleBarRect.contains(pt) -> LRESULT(HTCAPTION)

            else -> LRESULT(HTCLIENT)
        }
    }
}