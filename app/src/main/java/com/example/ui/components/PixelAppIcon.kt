package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Bitmap
import androidx.collection.LruCache
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.model.AppModel
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow

/**
 * Pixel Superellipse Squircle Shape.
 * Provides continuous curvature geometry conforming to Google Pixel adaptive icon guidelines.
 * Caches outline per size to avoid CPU path recalculations.
 */
class PixelSquircleShape(
    private val smoothness: Float = 0.68f
) : Shape {
    private var lastSize: Size? = null
    private var cachedOutline: Outline? = null

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        if (size == lastSize && cachedOutline != null) {
            return cachedOutline!!
        }
        val w = size.width
        val h = size.height
        val minDim = minOf(w, h)
        val r = minDim * 0.285f
        val c = r * smoothness

        val path = Path().apply {
            moveTo(r, 0f)
            lineTo(w - r, 0f)
            cubicTo(w - r + c, 0f, w, r - c, w, r)
            lineTo(w, h - r)
            cubicTo(w, h - r + c, w - r + c, h, w - r, h)
            lineTo(r, h)
            cubicTo(r - c, h, 0f, h - r + c, 0f, h - r)
            lineTo(0f, r)
            cubicTo(0f, r - c, r - c, 0f, r, 0f)
            close()
        }
        val outline = Outline.Generic(path)
        lastSize = size
        cachedOutline = outline
        return outline
    }
}

/**
 * Pixel Teardrop Shape for dynamic icon customization.
 * Caches outline per size.
 */
class PixelTeardropShape : Shape {
    private var lastSize: Size? = null
    private var cachedOutline: Outline? = null

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        if (size == lastSize && cachedOutline != null) {
            return cachedOutline!!
        }
        val w = size.width
        val h = size.height
        val r = minOf(w, h) * 0.5f
        val path = Path().apply {
            moveTo(r, 0f)
            lineTo(w - r, 0f)
            cubicTo(w, 0f, w, r, w, r)
            lineTo(w, h - 8f)
            cubicTo(w, h, w - 8f, h, w - 8f, h)
            lineTo(r, h)
            cubicTo(0f, h, 0f, h - r, 0f, h - r)
            lineTo(0f, r)
            cubicTo(0f, 0f, r, 0f, r, 0f)
            close()
        }
        val outline = Outline.Generic(path)
        lastSize = size
        cachedOutline = outline
        return outline
    }
}

// Reusable shape instances to avoid allocating new instances on every recomposition
private val squircleShapeInstance = PixelSquircleShape()
private val teardropShapeInstance = PixelTeardropShape()
private val roundedSquareShapeInstance = RoundedCornerShape(percent = 22)

/**
 * Resolves the dynamic icon mask shape based on user preference.
 * Defaults to the signature Pixel Squircle.
 */
fun getPixelMaskShape(shapeId: String): Shape {
    return when (shapeId) {
        "SQUIRCLE" -> squircleShapeInstance
        "ROUNDED_SQUARE" -> roundedSquareShapeInstance
        "CIRCLE" -> CircleShape
        "TEARDROP" -> teardropShapeInstance
        else -> squircleShapeInstance
    }
}

fun getSystemIconForApp(systemKey: String): ImageVector {
    return when (systemKey) {
        "phone" -> Icons.Default.Call
        "messages" -> Icons.Default.ChatBubble
        "chrome" -> Icons.Default.Language
        "camera" -> Icons.Default.CameraAlt
        "photos" -> Icons.Default.PhotoLibrary
        "google" -> Icons.Default.Android
        "gemini" -> Icons.Default.AutoAwesome
        "gmail" -> Icons.Default.Mail
        "maps" -> Icons.Default.Map
        "youtube" -> Icons.Default.PlayArrow
        "weather" -> Icons.Default.WbSunny
        "drive" -> Icons.Default.Folder
        "play_store" -> Icons.Default.Shop
        "clock" -> Icons.Default.Schedule
        "calculator" -> Icons.Default.Calculate
        "files" -> Icons.Default.Folder
        "settings" -> Icons.Default.Settings
        "notes" -> Icons.Default.NoteAlt
        "recorder" -> Icons.Default.Mic
        "studio" -> Icons.Default.Palette
        "home" -> Icons.Default.Home
        "contacts" -> Icons.Default.AccountCircle
        else -> Icons.Default.Android
    }
}

fun getStockAppColor(systemKey: String): Color {
    return when (systemKey) {
        "phone" -> GoogleGreen
        "messages" -> GoogleBlue
        "chrome" -> GoogleYellow
        "camera" -> Color(0xFF1E88E5)
        "photos" -> GoogleRed
        "google" -> GoogleBlue
        "gemini" -> Color(0xFF8E24AA)
        "gmail" -> GoogleRed
        "maps" -> GoogleGreen
        "youtube" -> GoogleRed
        "weather" -> Color(0xFF00ACC1)
        "drive" -> GoogleYellow
        "play_store" -> GoogleBlue
        "clock" -> Color(0xFF3949AB)
        "calculator" -> Color(0xFF43A047)
        "files" -> GoogleBlue
        "settings" -> Color(0xFF546E7A)
        "notes" -> GoogleYellow
        "recorder" -> Color(0xFFE53935)
        "studio" -> Color(0xFF8E24AA)
        "home" -> Color(0xFF00897B)
        "contacts" -> GoogleBlue
        else -> Color(0xFF00897B)
    }
}

object AppIconCache {
    private val memoryCache = LruCache<String, ImageBitmap>(250)

    fun get(packageName: String): ImageBitmap? = memoryCache.get(packageName)

    fun put(packageName: String, bitmap: ImageBitmap) {
        memoryCache.put(packageName, bitmap)
    }

    fun clear() {
        memoryCache.evictAll()
    }
}

private val appIconLabelStyle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Medium,
    color = Color.White,
    shadow = Shadow(
        color = Color.Black.copy(alpha = 0.8f),
        offset = Offset(0f, 2f),
        blurRadius = 4f
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PixelAppIcon(
    app: AppModel,
    modifier: Modifier = Modifier,
    useThemedIcons: Boolean = false,
    iconShape: String = "SQUIRCLE",
    showLabel: Boolean = true,
    iconSize: Dp = 54.dp,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current
    val maskShape = remember(iconShape) { getPixelMaskShape(iconShape) }
    val themeColor = MaterialTheme.colorScheme.primary
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val isPredefinedSystemApp = app.systemIconKey != "android"
    val iconVector = getSystemIconForApp(app.systemIconKey)
    val stockColor = getStockAppColor(app.systemIconKey)

    // Ultra-fast async icon loader: never blocks UI thread, instant from memory cache
    val realBitmap by produceState<ImageBitmap?>(
        initialValue = remember(app.packageName) { AppIconCache.get(app.packageName) },
        key1 = app.packageName
    ) {
        if (value != null || isPredefinedSystemApp) return@produceState
        val loaded = withContext(Dispatchers.IO) {
            try {
                val pm = context.packageManager
                val drawable = pm.getApplicationIcon(app.packageName)
                val bitmap = drawable.toBitmap(96, 96, Bitmap.Config.ARGB_8888)
                val img = bitmap.asImageBitmap()
                AppIconCache.put(app.packageName, img)
                img
            } catch (e: Throwable) {
                null
            }
        }
        value = loaded
    }

    Column(
        modifier = modifier
            .testTag("app_icon_${app.packageName}")
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    // Hardware accelerated graphics layer:
                    // RenderNode handles shape clipping and shadow on the GPU,
                    // accelerating heavy vector and bitmap iconography
                    shadowElevation = 2.dp.toPx()
                    shape = maskShape
                    clip = true
                }
                .background(
                    if (useThemedIcons) {
                        containerColor
                    } else {
                        if (isPredefinedSystemApp) stockColor else MaterialTheme.colorScheme.surfaceVariant
                    }
                )
                .border(
                    width = 0.5.dp,
                    color = if (useThemedIcons) {
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                    } else {
                        Color.White.copy(alpha = 0.15f)
                    },
                    shape = maskShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val currentBitmap = realBitmap
            if (isPredefinedSystemApp) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = app.label,
                    tint = if (useThemedIcons) themeColor else Color.White,
                    modifier = Modifier.size(iconSize * 0.54f)
                )
            } else if (currentBitmap != null) {
                if (useThemedIcons) {
                    Image(
                        bitmap = currentBitmap,
                        contentDescription = app.label,
                        colorFilter = ColorFilter.tint(themeColor, BlendMode.SrcIn),
                        modifier = Modifier.size(iconSize * 0.56f)
                    )
                } else {
                    Image(
                        bitmap = currentBitmap,
                        contentDescription = app.label,
                        modifier = Modifier.size(iconSize * 0.70f)
                    )
                }
            } else {
                Icon(
                    imageVector = iconVector,
                    contentDescription = app.label,
                    tint = if (useThemedIcons) themeColor else Color.White,
                    modifier = Modifier.size(iconSize * 0.54f)
                )
            }
        }

        if (showLabel) {
            Text(
                text = app.label,
                style = appIconLabelStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
