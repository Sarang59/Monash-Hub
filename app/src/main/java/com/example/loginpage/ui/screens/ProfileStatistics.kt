package com.example.loginpage.ui.screens

import android.R.attr.entries
import android.graphics.Color.toArgb
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.loginpage.R
import com.example.loginpage.ui.commonFunction.ThemePrefHelper
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.commonFunction.noDataAvailablePrompt
import com.example.loginpage.ui.commonFunction.generateShades
import com.example.loginpage.ui.data.model.Avatar
import com.example.loginpage.ui.data.model.Post
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.example.loginpage.ui.theme.saveAxis
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.String
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun ProfileStatistics(navController: NavController, drawerState: DrawerState)
{
    val colorScheme = MaterialTheme.colorScheme
    var isDataLoaded by remember { mutableStateOf(false) }

    // Full screen scrollable column
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = colorScheme.primaryBg
    ) {
        Column() {
            // Header of the page
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
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                        .clickable { navController.navigate("profile") },
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
                        text = "Profile Statistics",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primaryText
                        )
                    )
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                var postList = remember { mutableStateListOf<Post>() }
                val storedUserId = UserIdHelper.getUserId(LocalContext.current)

                // Connect to the firebase and fetch the posts data
                LaunchedEffect(storedUserId)
                {
                    Firebase.firestore.collection("post")
                        .whereEqualTo("post_created_by", storedUserId)
                        .get()
                        .addOnSuccessListener { userDocuments ->
                            if(userDocuments.isEmpty){
                                isDataLoaded = false
                            } else {
                                // Clear previous records to avoid duplication
                                postList.clear()

                                for(post in userDocuments) {
                                    val fetchedPost = post.toObject(Post::class.java)
                                    Log.d("Success", "Post fetched Successfully")
                                    postList.add(fetchedPost)
                                }

                                isDataLoaded = true
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Error: ", "Failed to fetch posts")
                        }
                }

                // Display error message or else display statistics
                if(!isDataLoaded) {
                    noDataAvailablePrompt("No data available")
                }
                else {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                    // Get today's date and subtract 7 days to get the start date
                    val calendar = Calendar.getInstance()
                    val endDate = calendar.time // Today
                    calendar.add(Calendar.DAY_OF_YEAR, -6)
                    val startDate = calendar.time

                    // Initialize lists to hold formatted date strings
                    val dateList = mutableListOf<String>()
                    val displayDateList = mutableListOf<String>()
                    val calendarIterator = Calendar.getInstance().apply { time = startDate }

                    // Loop through each day from startDate to endDate (inclusive)
                    while (!calendarIterator.time.after(endDate)) {
                        val dateStr = dateFormat.format(calendarIterator.time)
                        dateList.add(dateStr)
                        displayDateList.add(displayFormat.format(calendarIterator.time))

                        // Move on to the next day
                        calendarIterator.add(Calendar.DAY_OF_YEAR, 1)
                    }

                    // Group by formatted date string (yyyy-MM-dd)
                    val groupedByDate = postList.groupBy { post ->
                        val date = post.post_created_at?.toDate() ?: Date(0)
                        dateFormat.format(date)
                    }

                    // Compute stats for each day, ensuring all 7 days are included
                    val statsByDate = mutableMapOf<String, Map<String, Int>>()
                    for (dateStr in dateList) {
                        val postsOnDate = groupedByDate[dateStr] ?: emptyList()
                        val totalLikes = postsOnDate.sumOf { it.post_liked.size }
                        val totalSaves = postsOnDate.sumOf { it.post_saved.size }

                        statsByDate[dateStr] = mapOf(
                            "total_posts" to postsOnDate.size,
                            "total_likes" to totalLikes,
                            "total_saves" to totalSaves
                        )
                    }

                    // Sort the stats by date
                    val sortedStatsByDate = statsByDate.toSortedMap(compareBy { dateFormat.parse(it) })

                    // Sort by date and prepare line chart data
                    val entriesLikes = ArrayList<Entry>()
                    val entriesSaves = ArrayList<Entry>()
                    val labels = ArrayList<String>()

                    val sortedEntries = sortedStatsByDate.toSortedMap()

                    // Iterate over the entries to combine all the posts in a single record
                    var index = 0f
                    for ((dateStr, stats) in sortedEntries) {
                        val date = dateFormat.parse(dateStr)
                        val formattedLabel = displayFormat.format(date!!)
                        labels.add(formattedLabel)

                        val totalLikes = stats["total_likes"] as Int
                        val totalSaves = stats["total_saves"] as Int

                        entriesLikes.add(Entry(index, totalLikes.toFloat()))
                        entriesSaves.add(Entry(index, totalSaves.toFloat()))
                        index += 1f
                    }

                    // Create LineDataSet for Likes
                    val likesDataSet = LineDataSet(entriesLikes, "Likes").apply {
                        color = colorScheme.primaryText.toArgb()
                        valueTextColor = colorScheme.primaryText.toArgb()
                        lineWidth = 2f
                        circleRadius = 4f
                        setDrawValues(false)
                    }

                    // Create LineDataSet for Saves
                    val savesDataSet = LineDataSet(entriesSaves, "Saves").apply {
                        color = colorScheme.saveAxis.toArgb()
                        valueTextColor = colorScheme.primaryText.toArgb()
                        lineWidth = 2f
                        circleRadius = 4f
                        setDrawValues(false)
                    }

                    // Combine into LineData
                    val lineData = LineData(likesDataSet, savesDataSet)

                    // Draw a Pie chart
                    // Group the data as per the column mentioned
                    val postsByCategory = postList.groupingBy { it.post_category }.eachCount()
                    val chartBackgroundColor = colorScheme.Transparent.toArgb()
                    val chartBorderColor = colorScheme.inactiveGrey.toArgb()
                    val axisText = colorScheme.primaryText.toArgb()

                    // Convert map to PieEntry list
                    val totalPosts = postsByCategory.values.sum().toFloat()
                    val pieEntries = postsByCategory.map { (category, count) ->
                        val percentage = (count / totalPosts).toFloat() * 100
                        PieEntry(percentage, category)
                    }
                val context = LocalContext.current
                val isDarkTheme = ThemePrefHelper.isDark(context)
                val pieColors = generateShades(colorScheme.primaryText.toArgb(), pieEntries.size, isDarkTheme)

                    // Create PieDataSet and customize
                    val pieDataSet = PieDataSet(pieEntries, "").apply {
                        colors = pieColors.toList()
                        valueTextSize = 40f
                        xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
                        yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
                        valueFormatter = PercentValueFormatter()
                    }

                    val totalLikesAll = entriesLikes.sumOf { it.y.toInt() }
                    val totalSavesAll = entriesSaves.sumOf { it.y.toInt() }

                    // Wrap in PieData
                    val pieData = PieData(pieDataSet)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Total posts: " + postList.size,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    color = colorScheme.primaryText,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            )

                            if(totalLikesAll > 0 || totalSavesAll > 0) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Post Summary: Saves vs Likes",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.primaryText
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    AndroidView(
                                        factory = { context ->
                                            LineChart(context).apply {
                                                layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    400  // height in pixels
                                                )

                                                this.data = lineData
                                                this.description.isEnabled = false
                                                this.setTouchEnabled(true)
                                                this.setPinchZoom(true)
                                                setBackgroundColor(chartBackgroundColor) // Use the captured color
                                                setBorderColor(chartBorderColor)    // Border color if needed
                                                this.legend.isEnabled = true
                                                animateY(2000)

                                                // Configure X-Axis
                                                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                                                xAxis.position = XAxis.XAxisPosition.BOTTOM
                                                xAxis.setDrawGridLines(false)
                                                xAxis.labelRotationAngle = -45f
                                                xAxis.granularity = 1f
                                                xAxis.labelCount = labels.size
                                                xAxis.textColor = axisText
                                                axisLeft.textColor = axisText
                                                axisRight.isEnabled = false

                                                // Configure Legend
                                                this.legend.form = Legend.LegendForm.LINE          // Shape of legend marker
                                                this.legend.textSize = 12f
                                                this.legend.textColor = axisText
                                                this.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                                                this.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                                                this.legend.orientation = Legend.LegendOrientation.VERTICAL
                                                this.legend.setDrawInside(false)                   // Draw outside plot area
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Post Summary: Posts per category",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.primaryText
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                AndroidView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    factory = { context ->
                                        PieChart(context).apply {
                                            this.data = pieData
                                            this.description.isEnabled = false
                                            this.setDrawCenterText(true)
                                            this.centerText = "Posts by Category"
                                            this.setEntryLabelColor(axisText)
                                            this.setEntryLabelTextSize(12f)

                                            // Configure Legend
                                            this.legend.isEnabled = true
                                            this.legend.form = Legend.LegendForm.SQUARE
                                            this.legend.textSize = 12f
                                            this.legend.textColor = axisText
                                            this.legend.orientation = Legend.LegendOrientation.VERTICAL
                                            this.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                                            this.legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                                            this.legend.setDrawInside(false)

                                            animateY(2000)
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(26.dp))
                        }
                    }
                }
            }
        }
    }
}

//Used this class for formatting value (adding % sign)
class PercentValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String
    {
        return "${value.toInt()}%"
    }
}
