package com.example.phase1.ui.communityscreen

import android.graphics.Bitmap

/**
 * Helper object to pass a copied community image (as a Bitmap)
 * to the MainScreen. Only one bitmap is stored at a time.
 */
object CommunityImageHolder {
    var bitmap: Bitmap? = null
}
