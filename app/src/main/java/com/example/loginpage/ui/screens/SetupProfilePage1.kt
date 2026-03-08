package com.example.loginpage.ui.screens

import android.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.ui.data.model.UserProfile
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.gson.Gson
import java.net.URLDecoder
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupProfilePage1(navController: NavController,
                      email: String,
                      name: String,
                      avatarId: String) {
    // Variables
    var course by remember { mutableStateOf("") }
    var selectedCampus by remember { mutableStateOf("") }
    var selectedFaculty by remember { mutableStateOf("") }

    val campuses = listOf("Clayton", "Caulfield", "Peninsula", "Parkville")
    val faculties = listOf("Faculty of Art Design & Architecture (MADA)",
                            "Faculty of Arts",
                            "Faculty of Business",
                            "Faculty of Education",
                            "Faculty of Engineering",
                            "Faculty of Information Technology",
                            "Faculty of Law",
                            "Faculty of Medicine, Nursing and Health Sciences",
                            "Faculty of Pharmacy and Pharmaceutical Sciences",
                            "Faculty of Science")

    val artDesignArchCourses = listOf("Bachelor of Architectural Design",
                            "Bachelor of Design",
                            "Bachelor of Fine Art",
                            "Bachelor of Fine Art (Honours)",
                            "Bachelor of Architectural Design and Master of Architecture",
                            "Bachelor of Arts and Bachelor of Fine Art",
                            "Bachelor of Design and Bachelor of Arts",
                            "Bachelor of Design and Bachelor of Business",
                            "Bachelor of Design and Bachelor of Information Technology",
                            "Bachelor of Design and Bachelor of Media Communication",
                            "Bachelor of Education (Honours) and Bachelor of Fine Art",
                            "Bachelor of Engineering (Honours) and Bachelor of Architectural Design",
                            "Bachelor of Engineering (Honours) and Bachelor of Design",
                            "Bachelor of Fine Art and Bachelor of Business",
                            "Bachelor of Fine Art and Bachelor of Information Technology",
                            "Bachelor of Fine Art and Bachelor of Media Communication",
                            "Diploma of Art and Design",
                            "Diploma of Art and Design",
                            "Monash University Foundation Year - Extended",
                            "Monash University Foundation Year - Intensive",
                            "Monash University Foundation Year - Standard",
                            "Master of Architecture",
                            "Master of Arts",
                            "Master of Design",
                            "Master of Design (by Research)",
                            "Master of Fine Art",
                            "Master of Transport and Mobility Planning",
                            "Master of Urban Planning and Design",
                            "Doctor of Philosophy",
                            "Gender-Sensitive Training for Inclusive Placemaking"
    )

    val artCourses = listOf( "Bachelor of Arts",
        "Bachelor of Arts (Honours)",
        "Bachelor of Criminology",
        "Bachelor of Criminology and Policing",
        "Bachelor of Global Studies",
        "Bachelor of International Relations",
        "Bachelor of Media Communication",
        "Bachelor of Music",
        "Bachelor of Music (Honours)",
        "Bachelor of Politics, Philosophy and Economics",
        "Diploma of Languages",
        "Diploma of Liberal Arts",
        "Bachelor of Arts and Bachelor of Criminology",
        "Bachelor of Arts and Bachelor of Fine Art",
        "Bachelor of Arts and Bachelor of Global Studies",
        "Bachelor of Arts and Bachelor of Health Sciences",
        "Bachelor of Arts and Bachelor of Media Communication",
        "Bachelor of Arts and Bachelor of Music",
        "Bachelor of Business and Bachelor of Arts",
        "Bachelor of Business and Bachelor of Media Communication",
        "Bachelor of Commerce and Bachelor of Arts",
        "Bachelor of Commerce and Bachelor of Global Studies",
        "Bachelor of Commerce and Bachelor of Music",
        "Bachelor of Commerce and Bachelor of Politics, Philosophy and Economics",
        "Bachelor of Criminology and Bachelor of Information Technology",
        "Bachelor of Design and Bachelor of Arts",
        "Bachelor of Design and Bachelor of Media Communication",
        "Bachelor of Education (Honours) and Bachelor of Arts",
        "Bachelor of Education (Honours) and Bachelor of Music",
        "Bachelor of Engineering (Honours) and Bachelor of Arts",
        "Bachelor of Fine Art and Bachelor of Media Communication",
        "Bachelor of Global Studies and Bachelor of Information Technology",
        "Bachelor of Information Technology and Bachelor of Arts",
        "Bachelor of Laws (Honours) and Bachelor of Arts",
        "Bachelor of Laws (Honours) and Bachelor of Criminology",
        "Bachelor of Laws (Honours) and Bachelor of Global Studies",
        "Bachelor of Laws (Honours) and Bachelor of International Relations",
        "Bachelor of Laws (Honours) and Bachelor of Music",
        "Bachelor of Laws (Honours) and Bachelor of Politics, Philosophy and Economics",
        "Bachelor of Marketing and Bachelor of Arts",
        "Bachelor of Marketing and Bachelor of Media Communication",
        "Bachelor of Media Communication and Bachelor of Music",
        "Bachelor of Politics, Philosophy and Economics and Bachelor of Arts",
        "Bachelor of Psychology and Bachelor of Arts",
        "Bachelor of Science and Bachelor of Arts",
        "Bachelor of Science and Bachelor of Global Studies",
        "Bachelor of Science and Bachelor of Music",
        "Diploma of Arts",
        "Diploma of Arts",
        "Monash Access Program",
        "Monash University Foundation Year - Extended",
        "Monash University Foundation Year - Intensive",
        "Monash University Foundation Year - Standard",
        "Professional Certificate of Specialised Health and Legal Interpreting",
        "Graduate Certificate of Applied Linguistics",
        "Graduate Certificate of Bioethics",
        "Graduate Certificate of Communications and Media Studies",
        "Graduate Certificate of Cultural and Creative Industries",
        "Graduate Certificate of International Development Practice",
        "Graduate Certificate of International Relations",
        "Graduate Certificate of International Sustainable Tourism Management",
        "Graduate Certificate of Journalism",
        "Graduate Certificate of Marketing and Digital Communications",
        "Graduate Certificate of Public Policy",
        "Graduate Certificate of Strategic Communications Management",
        "Master of Applied Linguistics",
        "Master of Arts",
        "Master of Arts Research Training",
        "Master of Bioethics",
        "Master of Communications and Media Studies",
        "Master of Cultural and Creative Industries",
        "Master of Environment and Sustainability",
        "Master of International Development Practice",
        "Master of International Relations",
        "Master of International Sustainable Tourism Management",
        "Master of Interpreting and Translation Studies",
        "Master of Journalism",
        "Master of Marketing and Digital Communications",
        "Master of Public Policy",
        "Master of Strategic Communications Management",
        "Doctor of Philosophy"
    )

    val businessCourses = listOf( "Bachelor of Accounting",
        "Bachelor of Actuarial Science",
        "Bachelor of Banking and Finance",
        "Bachelor of Business",
        "Bachelor of Business Administration",
        "Bachelor of Commerce",
        "Bachelor of Commerce (Honours)",
        "Bachelor of Digital Business",
        "Bachelor of Economics",
        "Bachelor of Finance",
        "Bachelor of Marketing",
        "Bachelor of Actuarial Science and Master of Actuarial Studies",
        "Bachelor of Business and Bachelor of Accounting",
        "Bachelor of Business and Bachelor of Arts",
        "Bachelor of Business and Bachelor of Banking and Finance",
        "Bachelor of Business and Bachelor of Information Technology",
        "Bachelor of Business and Bachelor of Marketing",
        "Bachelor of Business and Bachelor of Media Communication",
        "Bachelor of Commerce and Bachelor of Actuarial Science",
        "Bachelor of Commerce and Bachelor of Arts",
        "Bachelor of Commerce and Bachelor of Biomedical Science",
        "Bachelor of Commerce and Bachelor of Computer Science",
        "Bachelor of Commerce and Bachelor of Economics",
        "Bachelor of Commerce and Bachelor of Finance",
        "Bachelor of Commerce and Bachelor of Global Studies",
        "Bachelor of Commerce and Bachelor of Information Technology",
        "Bachelor of Commerce and Bachelor of Music",
        "Bachelor of Commerce and Bachelor of Politics, Philosophy and Economics",
        "Bachelor of Commerce and Bachelor of Science",
        "Bachelor of Design and Bachelor of Business",
        "Bachelor of Digital Business and Bachelor of Business",
        "Bachelor of Digital Business and Bachelor of Information Technology",
        "Bachelor of Education (Honours) and Bachelor of Business",
        "Bachelor of Engineering (Honours) and Bachelor of Commerce",
        "Bachelor of Fine Art and Bachelor of Business",
        "Bachelor of Laws (Honours) and Bachelor of Commerce",
        "Bachelor of Marketing and Bachelor of Arts",
        "Bachelor of Marketing and Bachelor of Media Communication",
        "Diploma of Business",
        "Diploma of Business - Business",
        "Diploma of Business - Commerce",
        "Diploma of Business - Education",
        "Monash Access Program",
        "Monash University Foundation Year - Extended",
        "Monash University Foundation Year - Intensive",
        "Monash University Foundation Year - Standard",
        "Professional Certificate of Business Administration",
        "Graduate certificate",
        "Graduate Certificate of Analytics",
        "Graduate Certificate of Business",
        "Graduate Certificate of Business Administration (Digital)",
        "Graduate Certificate of Business Management",
        "Graduate Certificate of Project Management",
        "Graduate diploma",
        "Graduate Diploma of Business",
        "Graduate Diploma of Economic Analytics",
        "Graduate Diploma of Project Management",
        "Global Executive Master of Business Administration",
        "Master of Accounting",
        "Master of Actuarial Studies",
        "Master of Advanced Finance",
        "Master of Analytics",
        "Master of Applied Econometrics",
        "Master of Applied Marketing",
        "Master of Banking and Finance",
        "Master of Business",
        "Master of Business Administration (Digital)",
        "Master of Business Analytics",
        "Master of Business Management",
        "Master of Commerce",
        "Master of Economics",
        "Master of Environment and Sustainability",
        "Master of Global Business",
        "Master of Indigenous Business Leadership",
        "Master of Management",
        "Master of Management and Master of Behaviour and Systemic Change",
        "Master of Philosophy",
        "Master of Philosophy (Monash - Southeast)",
        "Master of Professional Accounting",
        "Master of Project Management",
        "Master of Regulation and Compliance",
        "Doctor of Philosophy"
    )

    val educationCourses = listOf("Bachelor of Education (Honours)",
        "Bachelor of Learning Design and Technology",
        "Bachelor of Education (Honours) and Bachelor of Arts",
        "Bachelor of Education (Honours) and Bachelor of Business",
        "Bachelor of Education (Honours) and Bachelor of Fine Art",
        "Bachelor of Education (Honours) and Bachelor of Music",
        "Bachelor of Education (Honours) and Bachelor of Science",
        "Diploma of Higher Education",
        "Diploma of Science - Education",
        "Diploma of Tertiary Studies",
        "Monash Access Program",
        "Monash University Foundation Year - Extended",
        "Monash University Foundation Year - Intensive",
        "Monash University Foundation Year - Standard",
        "Graduate Certificate of Counselling",
        "Graduate Certificate of Education Studies",
        "Graduate Certificate of Educational Design",
        "Graduate Certificate of Educational Research",
        "Graduate Certificate of Student Wellbeing",
        "Graduate Diploma of Professional Psychology",
        "Master of Applied Behaviour Analysis",
        "Master of Counselling",
        "Master of Education",
        "Master of Educational and Developmental Psychology",
        "Master of Educational and Developmental Psychology Advanced",
        "Master of Educational Leadership",
        "Master of Inclusive Education",
        "Master of Philosophy",
        "Master of Professional Psychology",
        "Master of Teaching",
        "Master of TESOL",
        "Doctor of Philosophy",
        "Master of Educational and Developmental Psychology and Doctor of Philosophy"
    )

    val engineeringCourses = listOf("Bachelor of Engineering (Honours)",
        "Bachelor of Engineering (Honours) and Bachelor of Architectural Design",
        "Bachelor of Engineering (Honours) and Bachelor of Arts",
        "Bachelor of Engineering (Honours) and Bachelor of Biomedical Science",
        "Bachelor of Engineering (Honours) and Bachelor of Commerce",
        "Bachelor of Engineering (Honours) and Bachelor of Computer Science",
        "Bachelor of Engineering (Honours) and Bachelor of Design",
        "Bachelor of Engineering (Honours) and Bachelor of Information Technology",
        "Bachelor of Engineering (Honours) and Bachelor of Pharmaceutical Science",
        "Bachelor of Engineering (Honours) and Bachelor of Science",
        "Bachelor of Engineering (Honours) and Master of Engineering",
        "Bachelor of Laws (Honours) and Bachelor of Engineering (Honours)",
        "Diploma of Engineering",
        "Diploma of Information Technology",
        "Monash University Foundation Year - Extended",
        "Monash University Foundation Year - Intensive",
        "Monash University Foundation Year - Standard",
        "Master of Advanced Engineering",
        "Master of Bioinformatics",
        "Master of Engineering",
        "Master of Engineering Science (Research)",
        "Master of Professional Engineering",
        "Master of Transport and Mobility Planning",
        "Doctor of Philosophy",
        "Doctor of Philosophy (Industry)"
    )

    val ITcourses = listOf("Bachelor of Computer Science",
        "Bachelor of Computer Science (Honours)",
        "Bachelor of Computer Science Advanced (Honours)",
        "Bachelor of Information Technology",
        "Bachelor of Business and Bachelor of Information Technology",
        "Bachelor of Commerce and Bachelor of Information Technology",
        "Bachelor of Criminology and Bachelor of Information Technology",
        "Bachelor of Design and Bachelor of Information Technology",
        "Bachelor of Engineering (Honours) and Bachelor of Computer Science",
        "Bachelor of Engineering (Honours) and Bachelor of Information Technology",
        "Bachelor of Fine Art and Bachelor of Information Technology",
        "Bachelor of Global Studies and Bachelor of Information Technology",
        "Bachelor of Information Technology and Bachelor of Arts",
        "Bachelor of Information Technology and Bachelor of Science",
        "Bachelor of Laws (Honours) and Bachelor of Computer Science",
        "Bachelor of Laws (Honours) and Bachelor of Information Technology",
        "Bachelor of Science and Bachelor of Computer Science",
        "Diploma of Information Technology",
        "Monash Access Program",
        "Monash University Foundation Year - Extended",
        "Monash University Foundation Year - Intensive",
        "Monash University Foundation Year - Standard",
        "Graduate Certificate of Applied Data Science",
        "Graduate Certificate of Computer Science",
        "Graduate Diploma of Applied Data Science",
        "Graduate Diploma of Computer Science",
        "Master of Applied Data Science",
        "Master of Artificial Intelligence",
        "Master of Bioinformatics",
        "Master of Business Information Systems",
        "Master of Computer Science",
        "Master of Cybersecurity",
        "Master of Data Science",
        "Master of Information Technology",
        "Master of Philosophy",
        "Doctor of Philosophy"
    )

    val lawCourses = listOf("Bachelor of Laws (Honours)",
        "Bachelor of Laws (Honours) and Bachelor of Arts",
        "Bachelor of Laws (Honours) and Bachelor of Biomedical Science",
        "Bachelor of Laws (Honours) and Bachelor of Commerce",
        "Bachelor of Laws (Honours) and Bachelor of Computer Science",
        "Bachelor of Laws (Honours) and Bachelor of Criminology",
        "Bachelor of Laws (Honours) and Bachelor of Engineering (Honours)",
        "Bachelor of Laws (Honours) and Bachelor of Global Studies",
        "Bachelor of Laws (Honours) and Bachelor of Information Technology",
        "Bachelor of Laws (Honours) and Bachelor of International Relations",
        "Bachelor of Laws (Honours) and Bachelor of Music",
        "Bachelor of Laws (Honours) and Bachelor of Politics, Philosophy and Economics",
        "Bachelor of Laws (Honours) and Bachelor of Psychology",
        "Bachelor of Laws (Honours) and Bachelor of Science",
        "Monash University Foundation Year - Extended",
        "Monash University Foundation Year - Intensive",
        "Monash University Foundation Year - Standard",
        "Graduate Certificate of Corporate and Financial Regulation",
        "Graduate Certificate of Employment Regulation",
        "Graduate Certificate of Human Rights",
        "Graduate Certificate of Laws",
        "Graduate Certificate of Legal Studies",
        "Graduate Certificate of Technology and Regulation",
        "Graduate Diploma of Australian Law",
        "Graduate Diploma of Laws",
        "Graduate Diploma of Legal Studies",
        "Juris Doctor",
        "Master of Laws",
        "Master of Legal Studies",
        "Master of Philosophy",
        "Doctor of Philosophy"
    )

    val healthScienceCourses = listOf("Bachelor of Biomedical Science",
        "Bachelor of Biomedical Science (Honours)",
        "Bachelor of Health Sciences",
        "Bachelor of Health Sciences (Honours)",
        "Bachelor of Medical Science (Honours)",
        "Bachelor of Nursing",
        "Bachelor of Nursing (Honours)",
        "Bachelor of Nutrition Science",
        "Bachelor of Occupational Therapy (Honours)",
        "Bachelor of Paramedicine",
        "Bachelor of Paramedicine (Honours)",
        "Bachelor of Physiotherapy (Honours)",
        "Bachelor of Psychology",
        "Bachelor of Psychology (Honours)",
        "Bachelor of Public Health",
        "Bachelor of Radiation Sciences",
        "Bachelor of Radiography and Medical Imaging (Honours)",
        "Bachelor of Speech Pathology (Honours)",
        "Bachelor of Arts and Bachelor of Health Sciences",
        "Bachelor of Commerce and Bachelor of Biomedical Science",
        "Bachelor of Engineering (Honours) and Bachelor of Biomedical Science",
        "Bachelor of Laws (Honours) and Bachelor of Biomedical Science",
        "Bachelor of Medical Science and Doctor of Medicine",
        "Bachelor of Medical Science and Doctor of Medicine",
        "Bachelor of Nursing and Bachelor of Midwifery (Honours)",
        "Bachelor of Psychology and Bachelor of Arts",
        "Bachelor of Psychology and Bachelor of Commerce",
        "Bachelor of Psychology and Bachelor of Science",
        "Bachelor of Science and Bachelor of Biomedical Science",
        "Monash University Foundation Year - Extended",
        "Monash University Foundation Year - Intensive",
        "Monash University Foundation Year - Standard",
        "Professional Certificate of Clinical Simulation",
        "Professional Certificate of Epidemiology",
        "Professional Certificate of Health Professions Education",
        "Professional Certificate of Human Behaviour and Applied Research",
        "Professional Certificate of Public Health",
        "Graduate Certificate of Addictive Behaviours",
        "Graduate Certificate of Aeromedical Retrieval",
        "Graduate Certificate of Clinical Simulation",
        "Graduate Certificate of Clinical Trials",
        "Graduate Certificate of Epidemiology",
        "Graduate Certificate of Epidemiology",
        "Graduate Certificate of Forensic Nursing and Midwifery",
        "Graduate Certificate of Health Administration",
        "Graduate Certificate of Health Management",
        "Graduate Certificate of Health Professions Education",
        "Graduate Certificate of Health Promotion",
        "Graduate Certificate of Magnetic Resonance Imaging",
        "Graduate Certificate of Perioperative Medicine",
        "Graduate Certificate of Personal Injury Management",
        "Graduate Certificate of Public Health",
        "Graduate Certificate of Wound Care",
        "Graduate Certificate of X-ray Image Interpretation",
        "Graduate Diploma in Psychology",
        "Graduate Diploma of Addictive Behaviours",
        "Graduate Diploma of Biostatistics",
        "Graduate Diploma of Clinical Trials",
        "Graduate Diploma of Health Management",
        "Graduate Diploma of Occupational and Environmental Health",
        "Graduate Diploma of Psychology Advanced",
        "Graduate Diploma of Reproductive Sciences",
        "Graduate Diploma of Wound Care",
        "Doctor of Physiotherapy",
        "Doctor of Podiatric Medicine",
        "Master of Addictive Behaviours",
        "Master of Advanced Clinical Nursing",
        "Master of Advanced Health Care Practice",
        "Master of Advanced Nursing",
        "Master of Bioinformatics",
        "Master of Biomedical and Health Science",
        "Master of Biomedical Science",
        "Master of Biostatistics",
        "Master of Biotechnology",
        "Master of Cardiovascular Perfusion",
        "Master of Clinical Embryology",
        "Master of Clinical Psychology",
        "Master of Clinical Research",
        "Master of Clinical Simulation",
        "Master of Critical Care Paramedicine",
        "Master of Forensic Medicine",
        "Master of Genome Analytics",
        "Master of Health Administration",
        "Master of Health Data Analytics",
        "Master of Health Management",
        "Master of Health Professions Education",
        "Master of Medical Ultrasound",
        "Master of Mental Health Science",
        "Master of Nursing Practice",
        "Master of Nutrition and Dietetics",
        "Master of Occupational and Environmental Health",
        "Master of Occupational Therapy Practice",
        "Master of Paramedic Practitioner",
        "Master of Perioperative Medicine",
        "Master of Philosophy",
        "Master of Public Health",
        "Master of Radiation Therapy",
        "Master of Reproductive Sciences",
        "Master of Social Work",
        "Master of Surgery",
        "Master of Wound Care",
        "Doctor of Philosophy",
        "Doctor of Philosophy (Clinical Neuropsychology)",
        "Doctor of Philosophy (Clinical Psychology)",
        "Doctor of Philosophy (Monash - Warwick)"
    )

    val pharmacyCourses = listOf("Bachelor of Pharmaceutical Science",
        "Bachelor of Pharmaceutical Science (Honours)",
        "Bachelor of Pharmaceutical Science Advanced (Honours)",
        "Bachelor of Pharmacy (Honours)",
        "Bachelor of Engineering (Honours) and Bachelor of Pharmaceutical Science",
        "Bachelor of Pharmacy (Honours) and Master of Pharmacy",
        "Monash University Foundation Year - Extended",
        "Monash University Foundation Year - Intensive",
        "Monash University Foundation Year - Standard",
        "Professional Certificate of Advanced Pharmacy Practice",
        "Graduate Certificate of Advanced Pharmacy Practice",
        "Graduate Certificate of Clinical Pharmacy (Aged Care)",
        "Graduate Certificate of Pharmacy Practice",
        "Master of Pharmaceutical Science",
        "Master of Philosophy",
        "Doctor of Philosophy"
    )

    val scienceCourses = listOf("Bachelor of Applied Data Science",
        "Bachelor of Applied Data Science Advanced (Honours)",
        "Bachelor of Science",
        "Bachelor of Science (Honours)",
        "Bachelor of Science Advanced - Global Challenges (Honours)",
        "Bachelor of Science Advanced - Research (Honours)",
        "Bachelor of Commerce and Bachelor of Science",
        "Bachelor of Education (Honours) and Bachelor of Science",
        "Bachelor of Engineering (Honours) and Bachelor of Science",
        "Bachelor of Information Technology and Bachelor of Science",
        "Bachelor of Laws (Honours) and Bachelor of Science",
        "Bachelor of Psychology and Bachelor of Science",
        "Bachelor of Science and Bachelor of Arts",
        "Bachelor of Science and Bachelor of Biomedical Science",
        "Bachelor of Science and Bachelor of Computer Science",
        "Bachelor of Science and Bachelor of Global Studies",
        "Bachelor of Science and Bachelor of Music",
        "Diploma of Higher Education",
        "Diploma of Science",
        "Diploma of Science - Education",
        "Monash University Foundation Year - Extended",
        "Monash University Foundation Year - Intensive",
        "Monash University Foundation Year - Standard",
        "Graduate Certificate of Behaviour Change",
        "Graduate Certificate of Food Science and Agribusiness",
        "Graduate Certificate of Genome Analytics",
        "Graduate Certificate of Green Chemistry and Sustainable Technologies",
        "Graduate Certificate of Innovation for Sustainability",
        "Graduate Diploma of Genome Analytics",
        "Master of Behaviour and Systemic Change",
        "Master of Bioinformatics",
        "Master of Environment and Sustainability",
        "Master of Environment and Sustainability (Double Masters with Shanghai Jiao Tong University)",
        "Master of Financial Mathematics",
        "Master of Food Science and Agribusiness",
        "Master of Genome Analytics",
        "Master of Geographical Information Science and Technology",
        "Master of Green Chemistry and Sustainable Technologies",
        "Master of Green Chemistry and Sustainable Technologies (Double Masters with Shanghai Jiao Tong Uni)",
        "Master of Management and Master of Behaviour and Systemic Change",
        "Master of Mathematics",
        "Master of Philosophy",
        "Master of Science",
        "Doctor of Philosophy",
        "Doctor of Philosophy (Monash - IITB)",
        "Doctor of Philosophy (Monash - Leipzig)",
        "Doctor of Philosophy (Monash - Southeast University)",
        "Doctor of Philosophy (Monash - University of Warwick)",
        "Doctor of Philosophy (Monash-Bayreuth)"
    )

    var facultyDropDownExpanded by remember { mutableStateOf(false) }
    var courseNameDropDownExpanded by remember { mutableStateOf(false) }
    var campusDropDownExpanded by remember { mutableStateOf(false) }
    var graduationYearDropDownExpanded by remember { mutableStateOf(false) }

    val currentYear = LocalDate.now().year
    val years = (currentYear..currentYear + 5).map { it.toString() }
    var selectedYear by remember { mutableStateOf("") }
    var courseList by remember { mutableStateOf<List<String>>(emptyList()) }

    var isFacultyNotSelected by remember { mutableStateOf( false ) }
    var isCourseNotSelected by remember { mutableStateOf( false ) }
    var isCampusNotSelected by remember { mutableStateOf( false ) }
    var isGraduationYearNotSelected by remember { mutableStateOf( false ) }
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primaryBg)
    ) {

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Header of the page
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to MonashHub",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Secure, social and student-centered",
                    fontSize = 16.sp,
                    color = colorScheme.primaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = colorScheme.primaryText
                )
            }

            Text(
                text = "Please provide your educational details",
                fontSize = 16.sp,
                color = colorScheme.primaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )

            //Faculty
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
                        append("Faculty: ")
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

            // Display an error message if the faculty is not selected
            if(isFacultyNotSelected)
            {
                Text(
                    text = "Please select a Faculty",
                    color = colorScheme.errorText,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            ExposedDropdownMenuBox(
                expanded = facultyDropDownExpanded,
                onExpandedChange = { facultyDropDownExpanded = !facultyDropDownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedFaculty,
                    onValueChange = {
                        isFacultyNotSelected = false
                    },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = if (facultyDropDownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown Arrow",
                            tint = if (facultyDropDownExpanded) colorScheme.primaryText else colorScheme.inactiveGrey,      // Change color dynamically
                            modifier = Modifier.size(40.dp)                        // Set icon size
                        )
                    },
                    placeholder = { Text("Select your Faculty", color = colorScheme.inactiveGrey) },
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
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )

                ExposedDropdownMenu(
                    expanded = facultyDropDownExpanded,
                    onDismissRequest = { facultyDropDownExpanded = false },
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    faculties.forEach { faculty ->
                        DropdownMenuItem(
                            text = { Text(faculty) },
                            onClick = {
                                selectedFaculty = faculty
                                facultyDropDownExpanded = false
                                isFacultyNotSelected = false
                            }
                        )
                    }
                }
            }

            // A map variable which stores information about which courses
            // to display depending on selected faculty
            val courseMap = mapOf(
                "Faculty of Art Design & Architecture (MADA)" to artDesignArchCourses,
                "Faculty of Arts" to artCourses,
                "Faculty of Business" to businessCourses,
                "Faculty of Education" to educationCourses,
                "Faculty of Engineering" to engineeringCourses,
                "Faculty of Information Technology" to ITcourses,
                "Faculty of Law" to lawCourses,
                "Faculty of Medicine, Nursing and Health Sciences" to healthScienceCourses,
                "Faculty of Pharmacy and Pharmaceutical Sciences" to pharmacyCourses,
                "Faculty of Science" to scienceCourses
            )

            courseList = courseMap[selectedFaculty] ?: emptyList()

            //Course Name
            if(selectedFaculty.isNotEmpty()) {
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
                            append("Course Name: ")
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

                // Display an error message if course name is not selected
                if (isCourseNotSelected) {
                    Text(
                        text = "Please select a course name from drop down menu",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = courseNameDropDownExpanded,
                    onExpandedChange = { courseNameDropDownExpanded = !courseNameDropDownExpanded }
                ) {
                    OutlinedTextField(
                        value = course,
                        onValueChange = {
                            isCourseNotSelected = false
                        },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = if (courseNameDropDownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                contentDescription = "Dropdown Arrow",
                                tint = if (courseNameDropDownExpanded) colorScheme.primaryText else colorScheme.inactiveGrey,      // Change color dynamically
                                modifier = Modifier.size(40.dp)                        // Set icon size
                            )
                        },
                        placeholder = {
                            Text(
                                "Select your Course",
                                color = colorScheme.inactiveGrey
                            )
                        },
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
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = courseNameDropDownExpanded,
                        onDismissRequest = { courseNameDropDownExpanded = false },
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        courseList.forEach { courseName ->
                            DropdownMenuItem(
                                text = { Text(courseName) },
                                onClick = {
                                    course = courseName
                                    courseNameDropDownExpanded = false
                                    isCourseNotSelected = false
                                }
                            )
                        }
                    }
                }
            }

            //Primary Campus
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
                        append("Primary Campus: ")
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

            // Display an error message if the campus is not selected
            if(isCampusNotSelected)
            {
                Text(
                    text = "Please select a campus",
                    color = colorScheme.errorText,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            ExposedDropdownMenuBox(
                expanded = campusDropDownExpanded,
                onExpandedChange = { campusDropDownExpanded = !campusDropDownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCampus,
                    onValueChange = {
                        isCampusNotSelected = false
                    },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = if (campusDropDownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown Arrow",
                            tint = if (campusDropDownExpanded) colorScheme.primaryText else colorScheme.inactiveGrey,      // Change color dynamically
                            modifier = Modifier.size(40.dp)                        // Set icon size
                        )
                    },
                    placeholder = { Text("Select your primary campus", color = colorScheme.inactiveGrey) },
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
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )

                ExposedDropdownMenu(
                    expanded = campusDropDownExpanded,
                    onDismissRequest = { campusDropDownExpanded = false },
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    campuses.forEach { campus ->
                        DropdownMenuItem(
                            text = { Text(campus) },
                            onClick = {
                                selectedCampus = campus
                                isCampusNotSelected = false
                                campusDropDownExpanded = false
                            }
                        )
                    }
                }
            }


            //Graduation Year
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
                        append("Graduation Year: ")
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

            // Display an error message if graduation is not selected
            if(isGraduationYearNotSelected)
            {
                Text(
                    text = "Please select a graduation year",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            ExposedDropdownMenuBox(
                expanded = graduationYearDropDownExpanded,
                onExpandedChange = { graduationYearDropDownExpanded = !graduationYearDropDownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedYear,
                    onValueChange = {
                        isGraduationYearNotSelected = false
                    },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = if (graduationYearDropDownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown Arrow",
                            tint = if (graduationYearDropDownExpanded) colorScheme.primaryText else colorScheme.inactiveGrey,
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    placeholder = { Text("Select your graduation year", color = colorScheme.inactiveGrey) },
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
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )

                ExposedDropdownMenu(
                    expanded = graduationYearDropDownExpanded,
                    onDismissRequest = { graduationYearDropDownExpanded = false },
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    years.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year) },
                            onClick = {
                                selectedYear = year
                                graduationYearDropDownExpanded = false
                                isGraduationYearNotSelected = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    // Check if the variables are empty or not
                    val fields = listOf(
                        selectedFaculty to { isFacultyNotSelected = true },
                        course to { isCourseNotSelected = true },
                        selectedCampus to { isCampusNotSelected = true },
                        selectedYear to { isGraduationYearNotSelected = true }
                    )

                    for ((value, setError) in fields) {
                        if (value.isEmpty()) {
                            setError()
                            return@Button
                        }
                    }

                    // If all conditions are met redirect the user to next setup page
                    if(!isCourseNotSelected && !isFacultyNotSelected
                        && !isCampusNotSelected && !isGraduationYearNotSelected) {
                        navController.navigate("setupPage2/$email/$name/$avatarId/" +
                                "$selectedFaculty/$course/$selectedCampus/$selectedYear")
                    }
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(150.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
            ) {
                Text("Next", color = colorScheme.primaryBg, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

        }
    }
}