package com.example.loginpage.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.loginpage.ui.data.model.Section
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    navController: NavController,
    drawerState: DrawerState
) {
    val colorScheme = MaterialTheme.colorScheme

    //Object Of Content
    val sections = listOf(
        Section(
            "1. Data Collection",
            "We collect limited personal data such as student ID, preferences, and usage statistics. This data helps personalize your experience within the Monash Hub."
        ),
        Section(
            "2. Use of Data",
            "Collected data is used solely for improving app features, providing recommendations, and enhancing user experience. It is never sold to third parties."
        ),
        Section(
            "3. Location Information",
            "Monash Hub uses location services to provide real-time campus navigation and localized content. Location data is only stored temporarily and not shared externally."
        ),
        Section(
            "4. Data Storage and Security",
            "Your data is securely stored in encrypted databases compliant with Monash University IT policies. Access is restricted to authorized personnel only."
        ),
        Section(
            "5. Third-Party Services",
            "We use third-party APIs such as Google Maps. These services may collect data in accordance with their respective privacy policies."
        ),
        Section(
            "6. Data Retention",
            "We retain user data only for as long as it is necessary for operational purposes or as required by university regulations."
        ),
        Section(
            "7. User Control",
            "You may contact support to request data deletion or access a summary of your stored data."
        ),
        Section(
            "8. Changes to Privacy Policy",
            "This policy may be updated periodically. Users will be notified of significant changes."
        ),
        Section(
            "9. Contact",
            "If you have questions or concerns, email us at: noreply.monashhub@gmail.com"
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primaryBg)
    ) {
        Column {
            // Top header with back navigation and consistent spacing
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
                        .clickable { navController.navigate("settings") },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back arrow",
                        tint = colorScheme.primaryText,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "Privacy Policy",
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

                LazyColumn {
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
