package com.example.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrightOrange
import com.example.ui.theme.CorporateNavy
import java.io.File

@Composable
fun FranchiseAvatar(
    selfiePath: String?,
    size: Dp = 44.dp,
    showCameraBadge: Boolean = true,
    testTag: String = "franchise_avatar",
    onClick: () -> Unit
) {
    val bitmap = remember(selfiePath) {
        if (!selfiePath.isNullOrEmpty()) {
            val file = File(selfiePath)
            if (file.exists()) {
                try {
                    BitmapFactory.decodeFile(file.absolutePath)
                } catch (_: Exception) {
                    null
                }
            } else null
        } else null
    }

    Box(
        modifier = Modifier
            .size(size)
            .testTag(testTag)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            // Saved Selfie Image
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Franchise Profile Selfie",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, BrightOrange, CircleShape)
            )
        } else {
            // Letter "F" placeholder before selfie is saved
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(CorporateNavy)
                    .border(2.dp, BrightOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "F",
                    color = Color.White,
                    fontSize = (size.value * 0.45f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Camera badge indicating clickable avatar to change/capture selfie
        if (showCameraBadge && size >= 40.dp) {
            Box(
                modifier = Modifier
                    .size(size * 0.35f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(BrightOrange)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change Selfie",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.2f)
                )
            }
        }
    }
}
