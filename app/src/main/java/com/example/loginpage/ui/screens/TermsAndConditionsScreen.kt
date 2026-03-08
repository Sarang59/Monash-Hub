package com.example.loginpage.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.ui.data.model.Section
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    navController: NavController,
    drawerState: DrawerState
) {
    //Object Of Terms and Conditions
    val sections = listOf(
        Section(
            "1. Acceptance of Terms",
            "By accessing or using Monash Hub, you confirm that you agree to abide by these Terms and Conditions. If you disagree, you must not use the app."
        ),
        Section(
            "2. Use of the App",
            "This app is intended for Monash University students to access campus navigation, resources, posts, and academic tools. You agree not to misuse the app or engage in unauthorized activities."
        ),
        Section(
            "3. User-Generated Content",
            "Posts shared within Monash Hub (including questions, discussions, and post content) are stored securely in our backend database. Content must be respectful, relevant, and adhere to Monash University's Code of Conduct. We reserve the right to moderate or remove content deemed inappropriate."
        ),
        Section(
            "4. Data Collection and Privacy",
            "We may collect your location, preferences, academic choices, and usage data to personalize your experience. All data is stored securely and is used only to enhance app functionality. No personal data is sold to third parties. Please refer to privacy for more details."
        ),
        Section(
            "5. Location Services",
            "The app requires access to your device's GPS to provide accurate directions and recommendations. You must grant permission to enable this feature."
        ),
        Section(
            "6. Google Maps Integration",
            "This app integrates Google Maps API. By using map features, you agree to Google's Terms of Service and Privacy Policy."
        ),
        Section(
            "7. Intellectual Property",
            "All content, designs, and code within Monash Hub are the intellectual property of the development team or Monash University. Unauthorized reproduction or redistribution is prohibited."
        ),
        Section(
            "8. Content Moderation",
            "User posts, comments, and shared content may be monitored for violations of guidelines. Offensive, spam, or harmful content may be removed without prior notice."
        ),
        Section(
            "9. Limitation of Liability",
            "We provide Monash Hub on an 'as-is' basis. We do not guarantee availability or accuracy and are not liable for any losses or issues arising from app use."
        ),
        Section(
            "10. Changes to Terms",
            "We reserve the right to modify these terms at any time. You will be notified of major updates. Continued use implies acceptance of the revised terms."
        ),
        Section(
            "11. Contact Us",
            "For support or questions regarding these Terms and Conditions, please contact us at: noreply.monashhub@gmail.com"
        )
    )

    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primaryBg)
    ) {
        Column {
            // Reuse the same custom Header you have in Settings screen
            Header(navController = navController, drawerState = drawerState)

            Spacer(modifier = Modifier.height(26.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp, horizontal = 0.dp)
                        .clickable{navController.navigate("settings")},
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back arrow",
                        tint = colorScheme.primaryText,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "Terms and Conditions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primaryText
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = colorScheme.primaryText
                )

                LazyColumn(
                ) {
                    item {
                        Text(
                            text = "Effective Date: May 15, 2025",
                            fontSize = 16.sp,
                            color = colorScheme.primaryText,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    items(sections) { section ->
                        Text(
                            text = section.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = colorScheme.primaryText,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = section.content,
                            fontSize = 16.sp,
                            color = colorScheme.primaryText,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
