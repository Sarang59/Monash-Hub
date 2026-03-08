package com.example.loginpage.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.loginpage.R
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.commonFunction.loadDrawableByName
import com.example.loginpage.ui.commonFunction.noDataAvailablePrompt
import com.example.loginpage.ui.data.model.Avatar
import com.example.loginpage.ui.data.model.CombineProfileAvatar
import com.example.loginpage.ui.data.model.Post
import com.example.loginpage.ui.data.model.UserProfile
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(navController: NavController, drawerState: DrawerState)
{
    val context = LocalContext.current
    val user_id = UserIdHelper.getUserId(context).toString()
    val colorScheme = MaterialTheme.colorScheme
    var avatarNotSelected by remember { mutableStateOf( false ) }
    var avatarList = remember { mutableStateListOf<Avatar>() }
    var selectedIndex by remember { mutableStateOf("") }
    val firestore = FirebaseFirestore.getInstance()
    var current_user by remember { mutableStateOf<CombineProfileAvatar?>(null) }
    var summary by remember { mutableStateOf("") }
    var isSummaryEmpty by remember { mutableStateOf(false) }
    var company by remember { mutableStateOf<String?>("") }
    var role by remember { mutableStateOf("") }
    var joiningDate by remember { mutableStateOf("") }
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    val today = remember { System.currentTimeMillis() }
    var isDateOfJoiningInvalid by remember { mutableStateOf(false) }
    var selectedMillis by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        //Fetch user data
        firestore.collection("user_profile")
            .whereEqualTo("user_id", user_id)
            .get()
            .addOnSuccessListener { userDocs ->
                val fetchedUser = userDocs.firstOrNull()?.toObject(UserProfile::class.java)

                if (fetchedUser != null) {
                    firestore.collection("avatar")
                        .whereEqualTo("avatar_id", fetchedUser.avatar_id)
                        .get()
                        .addOnSuccessListener { avatarDocs ->
                            var fetchedAvatar = avatarDocs.firstOrNull()?.toObject(Avatar::class.java)
                            selectedIndex = fetchedUser.avatar_id
                            summary = fetchedUser.profile_statement

                            if(fetchedUser.company_name.trim() != "\"\"") {
                                company = fetchedUser.company_name
                            }
                            if(fetchedUser.current_role.trim() != "\"\"") {
                                role = fetchedUser.current_role
                            }
                            fetchedUser.doj?.let { timestamp ->
                                selectedMillis = timestamp.toDate().time
                            }
                            val timestamp: Timestamp? = fetchedUser?.doj
                            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                            timestamp?.toDate()?.let { date ->
                                joiningDate = sdf.format(date)
                            }
                            current_user = CombineProfileAvatar(fetchedUser, fetchedAvatar)
                        }
                }
            }

        //Fetch all the avatars
        firestore.collection("avatar")
            .get()
            .addOnSuccessListener { avatarDocs ->
                for (avatar in avatarDocs) {
                    var finalAvatar = avatar.toObject(Avatar::class.java)
                    avatarList.add(finalAvatar)
                }
            }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedMillis ?: Instant.now().toEpochMilli()
    )

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = colorScheme.primaryBg
    ) {
        Column() {
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
                        .clickable{navController.navigate("profile")},
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
                        text = "Edit Profile",
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

                if(!avatarList.isEmpty() || current_user != null)
                {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colorScheme.primaryText
                                        )
                                    ) {
                                        append("Update your profile avatar: ")
                                    }

                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 18.sp,
                                            color = Color.Red
                                        )
                                    ) {
                                        append("*")
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        item{
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(5), // 5 avatars per row
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 100.dp, max = 400.dp),
                                horizontalArrangement = Arrangement.spacedBy(15.dp),
                                verticalArrangement = Arrangement.spacedBy(15.dp)
                            )
                            {
                                items(avatarList.size) { index ->
                                    val isSelected = avatarList[index].avatar_id == selectedIndex

                                    Image(
                                        painter = loadDrawableByName(
                                            context,
                                            avatarList[index].avatar_file_path
                                        ),
                                        contentDescription = "Avatar $index",
                                        colorFilter = if (!isSelected) {
                                            ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                                        } else null,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                avatarNotSelected = false
                                                selectedIndex = avatarList[index].avatar_id
                                            }
                                    )

                                }
                            }
                        }

                        item{
                            // Profile Summary
                            Spacer(modifier = Modifier.height(26.dp))
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colorScheme.primaryText
                                        )
                                    ) {
                                        append("Edit profile statement: ")
                                    }

                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 18.sp,
                                            color = colorScheme.errorText
                                        )
                                    ) {
                                        append("*")
                                    }
                                }
                            )

                            if(isSummaryEmpty)
                            {
                                Text(
                                    text = "Summary cannot be blank",
                                    color = Color.Red,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }

                            OutlinedTextField(
                                value = summary,
                                onValueChange = {
                                    if(it.length != 0){
                                        summary = it
                                        isSummaryEmpty = false
                                    }
                                    else{
                                        summary = it
                                        isSummaryEmpty = true
                                    }
                                },
                                placeholder = { Text(text = "Type here", color = colorScheme.inactiveGrey)},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .padding(vertical = 6.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = colorScheme.Transparent,
                                    unfocusedContainerColor = colorScheme.Transparent,
                                    errorContainerColor = colorScheme.errorText,
                                    focusedLabelColor = colorScheme.primaryText,
                                    unfocusedLabelColor = colorScheme.inactiveGrey,
                                    focusedIndicatorColor = colorScheme.primaryText,
                                    unfocusedIndicatorColor = colorScheme.inactiveGrey,
                                ),
                                shape = RectangleShape,
                            )
                        }

                        item{
                            //Company Name
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colorScheme.primaryText
                                        )
                                    ) {
                                        append("Company Name: ")
                                    }
                                }
                            )
                            OutlinedTextField(
                                value = company.toString(),
                                onValueChange = { newCompany: String -> company = newCompany },
                                placeholder = { Text("Enter your company name", color = colorScheme.inactiveGrey) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = colorScheme.Transparent,
                                    unfocusedContainerColor = colorScheme.Transparent,
                                    errorContainerColor = colorScheme.errorText,
                                    focusedLabelColor = colorScheme.primaryText,
                                    unfocusedLabelColor = colorScheme.inactiveGrey,
                                    focusedIndicatorColor = colorScheme.primaryText,
                                    unfocusedIndicatorColor = colorScheme.inactiveGrey,
                                ),
                                shape = RectangleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            )

                            //Role
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colorScheme.primaryText
                                        )
                                    ) {
                                        append("Current Role: ")
                                    }
                                }
                            )
                            OutlinedTextField(
                                value = role.toString(),
                                onValueChange = { newRole: String -> role = newRole },
                                placeholder = { Text("Enter your role ", color = inactiveGrey) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = colorScheme.Transparent,
                                    unfocusedContainerColor = colorScheme.Transparent,
                                    errorContainerColor = colorScheme.errorText,
                                    focusedLabelColor = colorScheme.primaryText,
                                    unfocusedLabelColor = colorScheme.inactiveGrey,
                                    focusedIndicatorColor = colorScheme.primaryText,
                                    unfocusedIndicatorColor = colorScheme.inactiveGrey,
                                ),
                                shape = RectangleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            )


                            // Date of joining
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colorScheme.primaryText
                                        )
                                    ) {
                                        append("Date of Joining: ")
                                    }
                                }
                            )

                            if(isDateOfJoiningInvalid)
                            {
                                Text(
                                    text = "Date of Joining is Invalid",
                                    color = Color.Red,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }

                            OutlinedTextField(
                                value = joiningDate,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDatePicker = true }
                                    .padding(vertical = 6.dp),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.CalendarMonth,
                                        contentDescription = "Select date",
                                        tint = colorScheme.inactiveGrey,
                                        modifier = Modifier
                                            .clickable { showDatePicker = true }
                                            .size(28.dp).padding(end = 2.dp)
                                    )
                                },
                                placeholder = { Text("Select your joining date", color = colorScheme.inactiveGrey) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = colorScheme.Transparent,
                                    unfocusedContainerColor = colorScheme.Transparent,
                                    errorContainerColor = colorScheme.errorText,
                                    focusedLabelColor = colorScheme.primaryText,
                                    unfocusedLabelColor = colorScheme.inactiveGrey,
                                    focusedIndicatorColor = colorScheme.primaryText,
                                    unfocusedIndicatorColor = colorScheme.inactiveGrey,
                                ),
                                shape = RectangleShape
                            )

                            if (showDatePicker) {
                                Dialog(onDismissRequest = { showDatePicker = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(16.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        color = colorScheme.primaryBg,
                                        tonalElevation = 8.dp
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .background(colorScheme.primaryBg)
                                                .padding(16.dp)
                                        ) {
                                            DatePicker(
                                                state = datePickerState,
                                                colors = DatePickerDefaults.colors(
                                                    containerColor = colorScheme.primaryBg,
                                                    titleContentColor = colorScheme.primaryText,
                                                    headlineContentColor = colorScheme.primaryText,
                                                    weekdayContentColor = colorScheme.primaryText,
                                                    subheadContentColor = colorScheme.primaryText,
                                                    selectedDayContainerColor = colorScheme.primaryText,
                                                    selectedDayContentColor = colorScheme.primaryBg
                                                )
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(colorScheme.primaryBg),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextButton(onClick = {
                                                    showDatePicker = false
                                                }) {
                                                    Text("Cancel", color = colorScheme.primaryText)
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                TextButton(onClick = {
                                                    val selected = datePickerState.selectedDateMillis
                                                    if (selected != null && selected <= today)
                                                    {
                                                        val zoneId = ZoneId.of("Australia/Melbourne")

                                                        val selectedLocalDate = Instant.ofEpochMilli(selected)
                                                            .atZone(zoneId)
                                                            .toLocalDate()

                                                        val midnightDateTime = selectedLocalDate.atStartOfDay(zoneId)
                                                        val dateAtMidnight = Date.from(midnightDateTime.toInstant())

                                                        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                                        formatter.timeZone = TimeZone.getTimeZone("Australia/Melbourne")

                                                        joiningDate = formatter.format(dateAtMidnight)
                                                        isDateOfJoiningInvalid = false
                                                    }
                                                    else
                                                    {
                                                        isDateOfJoiningInvalid = true
                                                    }

                                                    showDatePicker = false
                                                }) {
                                                    Text("OK", color = colorScheme.primaryText)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item{
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        if(!isSummaryEmpty && !isDateOfJoiningInvalid) {
                                            val sDF = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                            sDF.timeZone = TimeZone.getTimeZone("Australia/Melbourne")
                                            var dojTimestamp = Timestamp.now()

                                            if (!joiningDate.isEmpty()) {
                                                val parsedDate = sDF.parse(joiningDate)
                                                val calendar = Calendar.getInstance(TimeZone.getTimeZone("Australia/Melbourne"))
                                                calendar.time = parsedDate!!
                                                calendar.set(Calendar.HOUR_OF_DAY, 0)
                                                calendar.set(Calendar.MINUTE, 0)
                                                calendar.set(Calendar.SECOND, 0)
                                                calendar.set(Calendar.MILLISECOND, 0)

                                                dojTimestamp = Timestamp(calendar.time)
                                            }

                                            firestore.collection("user_profile")
                                                .whereEqualTo("user_id", user_id)
                                                .get()
                                                .addOnSuccessListener { documents ->
                                                    val doc = documents.firstOrNull()
                                                    if (doc != null) {
                                                        firestore.collection("user_profile")
                                                            .document(doc.id)
                                                            .update(
                                                                "avatar_id", selectedIndex,
                                                                "profile_statement", summary,
                                                                "company_name", company,
                                                                "current_role", role,
                                                                "doj", if(!joiningDate.isEmpty()) dojTimestamp else null
                                                            )
                                                            .addOnSuccessListener {
                                                                navController.navigate("successPage/editProfile")
                                                            }
                                                    }
                                                }
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .align(Alignment.Center)
                                        .width(100.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
                                ) {
                                    Text(
                                        "Save",
                                        color = colorScheme.primaryBg,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
                else{
                    noDataAvailablePrompt("No user data loaded")
                }
            }
        }
    }
}