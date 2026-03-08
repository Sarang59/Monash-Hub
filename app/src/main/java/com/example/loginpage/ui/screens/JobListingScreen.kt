package com.example.loginpage.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.sharp.FilterAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.ui.data.network.JobListingHelper
import com.example.loginpage.ui.commonFunction.noDataAvailablePrompt
import com.example.loginpage.ui.data.model.JobItem
import com.example.loginpage.ui.theme.cardBg
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListingScreen(navController: NavController, drawerState: DrawerState)
{
    // Variables
    var jobDetails by remember { mutableStateOf<List<JobItem>>(emptyList()) }
    val allJobs = mutableListOf<JobItem>()
    val context = LocalContext.current

    // List of countries
    val countries = listOf("au")

    // Credentials to connect to Adzuna external API to fetch data
    val appId = "6577857d"
    val apiKey = "9008952c2e6d50ddcce955c0522f6660"
    val colorScheme = MaterialTheme.colorScheme

    // Variables
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true )
    var showSheet by remember { mutableStateOf(false) }
    var contracts = remember { mutableListOf<String>() }
    var categories = remember { mutableListOf<String>() }
    var selectedCategories = remember { mutableListOf<String>() }
    var selectedContracts = remember { mutableListOf<String>() }

    // Fetch the data from external API and store it in a list
    LaunchedEffect(Unit) {
        for (country in countries) {
            // Get the response
            val response = JobListingHelper.apiService.getJobs(
                country = country,
                appId = appId,
                apiKey = apiKey
            )

            // If it is successful then populate the list
            if (response.isSuccessful) {
                val apiJobs = response.body()?.results ?: emptyList()
                allJobs += apiJobs.mapIndexed { index, job ->
                    val category = job.category?.label.toString()

                    if (category !in categories) {
                        categories.add(category)
                    }

                    if (!job.contract_type.isNullOrEmpty()) {
                        val contract = job.contract_type.replaceFirstChar { it.uppercase() }
                        if (contract !in contracts) {
                            contracts.add(job.contract_type.replaceFirstChar { it.uppercase() })
                        }
                    }

                    val jobItem = JobItem(
                        jobId = "${country}_${index + 1}",
                        jobTitle = job.title ?: "Title not provided",
                        jobContractType = if (job.contract_type.isNullOrEmpty()) "Contract not mentioned" else "Contract: ${job.contract_type.replaceFirstChar { it.uppercase() }}",
                        jobDescription = job.description ?: "No description",
                        jobUrl = job.redirect_url ?: "No URL",
                        jobSalary = if (job.salary_min.isNullOrEmpty()) "Salary not mentioned" else "Salary: $${job.salary_min}",
                        jobLocation = job.location?.display_name ?: "Location not available",
                        jobCategory = job.category?.label.toString()
                    )
                    jobItem
                }
            }
            // Throw an error if data is not fetched
            else {
                Log.e("Error", "Unable to fetch the API information")
            }
        }
        jobDetails = allJobs
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.primaryBg
    ) {
        Column() {
            // Display header
            Header(navController = navController,drawerState = drawerState)
            Spacer(modifier = Modifier.height(26.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp, horizontal = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Job Listings",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primaryText
                        )
                    )

                    // Check if list is not empty
                    if (!jobDetails.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = { showSheet = true }, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Sharp.FilterAlt,
                                contentDescription = "Filter Icon",
                                tint = colorScheme.primaryText,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        if (showSheet) {
                            ModalBottomSheet(
                                containerColor = colorScheme.primaryBg,
                                onDismissRequest = { showSheet = false },
                                sheetState = sheetState
                            ) {
                                JobBoardBottomFilterContent(
                                    categories,
                                    contracts,
                                    selectedCategories,
                                    selectedContracts
                                ) { newCategories, newContracts ->
                                    selectedCategories = newCategories as MutableList<String>
                                    selectedContracts = newContracts as MutableList<String>
                                    showSheet = false
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn()
                    {
                        if(jobDetails.isEmpty())
                        {
                            return@LazyColumn
                        }

                        items(jobDetails.size)
                        { index ->
                            val currentJob = jobDetails[index]
                            if (
                                (selectedCategories.size > 0 && currentJob.jobCategory in selectedCategories) ||
                                (selectedContracts.size > 0 && selectedContracts.any { currentJob.jobContractType.endsWith(it) }) ||
                                (selectedContracts.size == 0 && selectedCategories.size==0)
                            ) {
                                Card(
                                    shape = RectangleShape,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(6.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                                    colors = CardDefaults.cardColors(containerColor = colorScheme.cardBg)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(colorScheme.cardBg)
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    start = 12.dp,
                                                    top = 12.dp,
                                                    end = 0.dp,
                                                    bottom = 12.dp
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )
                                            {
                                                Text(
                                                    text = currentJob.jobTitle,
                                                    modifier = Modifier.padding(bottom = 10.dp),
                                                    color = colorScheme.primaryText,
                                                    style = TextStyle(
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            vertical = 0.dp,
                                                            horizontal = 0.dp
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.EditNote,
                                                        contentDescription = "Contract",
                                                        tint = colorScheme.primaryText,
                                                        modifier = Modifier.size(25.dp)
                                                    )
                                                    Text(
                                                        text = currentJob.jobContractType,
                                                        color = colorScheme.primaryText,
                                                        style = TextStyle(
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(5.dp))
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            vertical = 0.dp,
                                                            horizontal = 0.dp
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.LocationOn,
                                                        contentDescription = "Location",
                                                        tint = colorScheme.primaryText,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                    Text(
                                                        text = "Location: " + currentJob.jobLocation,
                                                        color = colorScheme.primaryText,
                                                        modifier = Modifier.padding(start = 5.dp),
                                                        style = TextStyle(
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(5.dp))
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            vertical = 0.dp,
                                                            horizontal = 0.dp
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Money,
                                                        contentDescription = "Salary",
                                                        tint = colorScheme.primaryText,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                    Text(
                                                        text = currentJob.jobSalary,
                                                        modifier = Modifier.padding(start = 5.dp),
                                                        color = colorScheme.primaryText,
                                                        style = TextStyle(
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))

                                                val truncatedJobDesc =
                                                    if (currentJob.jobDescription.length > 200) currentJob.jobDescription.take(
                                                        200
                                                    ) + "..." else currentJob.jobDescription

                                                Text(
                                                    text = truncatedJobDesc,
                                                    color = colorScheme.primaryText,
                                                    style = TextStyle(fontSize = 16.sp)
                                                )

                                                // A button to redirect to the URL page
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Button(
                                                    onClick = {
                                                        val intent = Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse(currentJob.jobUrl)
                                                        )
                                                        context.startActivity(intent)
                                                    },
                                                    modifier = Modifier
                                                        .padding(top = 10.dp)
                                                        .align(Alignment.CenterHorizontally)
                                                        .width(150.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = colorScheme.primaryText
                                                    )
                                                ) {
                                                    Text(
                                                        "View more",
                                                        color = colorScheme.primaryBg,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(26.dp))
                            }
                        }

                        item{
                            if(
                                !jobDetails.any { it1 ->
                                    (selectedCategories.size > 0 && it1.jobCategory in selectedCategories) ||
                                            (selectedContracts.size > 0 && selectedContracts.any { it1.jobContractType.endsWith(it) }) ||
                                            (selectedContracts.size == 0 && selectedCategories.size==0)
                                }
                            ) {
                                noDataAvailablePrompt("No job found for your preferences")
                            }
                        }
                    }
                }
            }
        }
    }
}