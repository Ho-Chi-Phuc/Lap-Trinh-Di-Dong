package com.example.lab2_ltdd.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab2_ltdd.R
import com.example.lab2_ltdd.ui.theme.CyanContainer
import com.example.lab2_ltdd.ui.theme.DeepNavy
import com.example.lab2_ltdd.ui.theme.ElectricCyan
import com.example.lab2_ltdd.ui.theme.Lab2_ltddTheme
import com.example.lab2_ltdd.ui.theme.NavyBackground
import com.example.lab2_ltdd.ui.theme.NavyCard
import com.example.lab2_ltdd.ui.theme.NavyCardBorder
import com.example.lab2_ltdd.ui.theme.SkyCyan
import com.example.lab2_ltdd.ui.theme.TextPrimary
import com.example.lab2_ltdd.ui.theme.TextSecondary

@Composable
fun BusinessCardScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DeepNavy,
                        NavyBackground,
                        Color(0xFF030712)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ProfileSection(
                name = "Hồ Chí Phúc",
                title = "Computer Engineering Student",
                specialization = "Robotics & Embedded Systems"
            )

            Spacer(modifier = Modifier.height(36.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.85f),
                thickness = 1.dp,
                color = ElectricCyan.copy(alpha = 0.25f)
            )

            Spacer(modifier = Modifier.height(36.dp))

            ContactInformation(
                phoneNumber = "0388187703",
                emailAddress = "chiphuc170406@gmail.com"
            )
        }
    }
}

@Composable
fun ProfileSection(
    name: String,
    title: String,
    specialization: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(116.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NavyCard,
                            DeepNavy
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            ElectricCyan,
                            SkyCyan,
                            ElectricCyan.copy(alpha = 0.2f),
                            ElectricCyan
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_robotics),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = SkyCyan,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CyanContainer,
            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ElectricCyan)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = specialization,
                    style = MaterialTheme.typography.labelLarge,
                    color = ElectricCyan,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ContactInformation(
    phoneNumber: String,
    emailAddress: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ContactItem(
            iconResId = R.drawable.ic_phone,
            label = "Số điện thoại",
            value = phoneNumber,
            onClick = {
                launchPhoneDialer(context, phoneNumber)
            }
        )

        ContactItem(
            iconResId = R.drawable.ic_email,
            label = "Email",
            value = emailAddress,
            onClick = {
                launchEmailClient(context, emailAddress)
            }
        )
    }
}

@Composable
fun ContactItem(
    iconResId: Int,
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavyCard
        ),
        border = BorderStroke(1.dp, NavyCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CyanContainer)
                        .border(1.dp, ElectricCyan.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = label,
                        tint = ElectricCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = SkyCyan.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun launchPhoneDialer(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Không tìm thấy ứng dụng gọi điện", Toast.LENGTH_SHORT).show()
    }
}

private fun launchEmailClient(context: Context, emailAddress: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$emailAddress")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Không tìm thấy ứng dụng email", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun BusinessCardScreenPreview() {
    Lab2_ltddTheme {
        BusinessCardScreen()
    }
}

