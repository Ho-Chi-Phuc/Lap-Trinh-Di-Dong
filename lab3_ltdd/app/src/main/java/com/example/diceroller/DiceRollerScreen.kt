package com.example.diceroller

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diceroller.ui.theme.DiceRollerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@Composable
fun DiceRollerScreen(modifier: Modifier = Modifier) {
    var result by remember { mutableIntStateOf(1) }
    var displayedFace by remember { mutableIntStateOf(1) }
    var isRolling by remember { mutableStateOf(false) }

    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val bounce = remember { Animatable(0f) }
    val shadowScale = remember { Animatable(1f) }
    val shadowAlpha = remember { Animatable(0.28f) }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current.density
    val isDark = isSystemInDarkTheme()

    val bgGradient = if (isDark) {
        listOf(
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF0F172A)
        )
    } else {
        listOf(
            Color(0xFFF8FAFC),
            Color(0xFFEEF2F6),
            Color(0xFFE2E8F0)
        )
    }

    val titleColor = if (isDark) Color(0xFF93C5FD) else Color(0xFF1E3A8A)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val resultCardBg = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    val resultTextColor = if (isDark) Color(0xFF60A5FA) else Color(0xFF1D4ED8)
    val shadowColor = if (isDark) Color(0xFF000000) else Color(0xFF0F172A)

    val diceImageRes = when (displayedFace) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgGradient))
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_dice_roller),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = titleColor,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.subtitle_dice_roller),
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleColor
                )
            }

            Box(
                modifier = Modifier.size(260.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .width(160.dp)
                        .height(28.dp)
                        .graphicsLayer {
                            scaleX = shadowScale.value
                            scaleY = shadowScale.value
                            alpha = shadowAlpha.value
                        }
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    shadowColor,
                                    Color.Transparent
                                )
                            )
                        )
                )

                Image(
                    painter = painterResource(diceImageRes),
                    contentDescription = stringResource(R.string.dice_content_description, displayedFace),
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer {
                            rotationZ = rotation.value
                            scaleX = scale.value
                            scaleY = scale.value
                            translationY = bounce.value * density
                        }
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = resultCardBg
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 32.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = result,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(150)) +
                                    scaleIn(initialScale = 0.9f, animationSpec = tween(150)))
                                    .togetherWith(fadeOut(animationSpec = tween(100)))
                            },
                            label = "ResultAnimation"
                        ) { targetResult ->
                            Text(
                                text = stringResource(R.string.result_label, targetResult),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = resultTextColor
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (isRolling) return@Button
                        isRolling = true
                        val targetResult = (1..6).random()

                        coroutineScope.launch {
                            val rotationJob = launch {
                                rotation.snapTo(0f)
                                rotation.animateTo(
                                    targetValue = 720f,
                                    animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
                                )
                                rotation.snapTo(0f)
                            }

                            val scaleJob = launch {
                                scale.animateTo(
                                    targetValue = 1.15f,
                                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                                )
                                scale.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                                )
                            }

                            val bounceJob = launch {
                                bounce.animateTo(
                                    targetValue = -30f,
                                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                                )
                                bounce.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                                )
                            }

                            val shadowScaleJob = launch {
                                shadowScale.animateTo(
                                    targetValue = 0.72f,
                                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                                )
                                shadowScale.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                                )
                            }

                            val shadowAlphaJob = launch {
                                shadowAlpha.animateTo(
                                    targetValue = 0.12f,
                                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                                )
                                shadowAlpha.animateTo(
                                    targetValue = 0.28f,
                                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                                )
                            }

                            val faceShuffleJob = launch {
                                val steps = 8
                                val interval = 65L
                                for (i in 0 until steps) {
                                    displayedFace = (1..6).random()
                                    delay(interval)
                                }
                                displayedFace = targetResult
                            }

                            joinAll(
                                rotationJob,
                                scaleJob,
                                bounceJob,
                                shadowScaleJob,
                                shadowAlphaJob,
                                faceShuffleJob
                            )

                            result = targetResult
                            displayedFace = targetResult
                            rotation.snapTo(0f)
                            scale.snapTo(1f)
                            bounce.snapTo(0f)
                            shadowScale.snapTo(1f)
                            shadowAlpha.snapTo(0.28f)
                            isRolling = false
                        }
                    },
                    enabled = !isRolling,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        disabledContainerColor = Color(0xFF2563EB).copy(alpha = 0.5f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 1.dp,
                        disabledElevation = 0.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(56.dp)
                ) {
                    Text(
                        text = if (isRolling) {
                            stringResource(R.string.rolling)
                        } else {
                            stringResource(R.string.roll_dice)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiceRollerPreview() {
    DiceRollerTheme {
        DiceRollerScreen()
    }
}
