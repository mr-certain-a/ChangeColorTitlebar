@file:Suppress("FunctionName", "NonAsciiCharacters")
package gui.jna

import com.sun.jna.platform.win32.BaseTSD.LONG_PTR
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser.WindowProc

interface MyUser32: User32 {
    companion object {
        const val GWLP_WNDPROC: Int = -4
    }

    fun SetWindowLongPtr(hwnd: HWND, index: Int, wndProc: WindowProc): LONG_PTR
    fun SetWindowLongPtr(hwnd: HWND, index: Int, wndProc: LONG_PTR): LONG_PTR
    fun CallWindowProc(proc: LONG_PTR, hwnd: HWND, uMsg: Int, wParam: WPARAM, lParam: LPARAM): LRESULT
}
