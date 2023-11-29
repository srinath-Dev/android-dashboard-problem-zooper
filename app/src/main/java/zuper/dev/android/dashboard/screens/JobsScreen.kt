package zuper.dev.android.dashboard.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import zuper.dev.android.dashboard.data.model.JobApiModel
import zuper.dev.android.dashboard.data.model.JobStatus
import zuper.dev.android.dashboard.data.remote.SampleData
import zuper.dev.android.dashboard.ui.theme.AppTheme
import zuper.dev.android.dashboard.ui.theme.Purple40
import zuper.dev.android.dashboard.ui.theme.subTitleTextColor
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun JobsScreen(navController: NavHostController) {

    ScaffoldWithTopBarJobs(navController)

}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopBarJobs(navController: NavHostController) {
    val jobData: List<JobApiModel> = SampleData.generateRandomJobList(20);
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Jobs")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                ),
            )
        }, content = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 58.dp)
                    .fillMaxSize()
                    .background(Color(0xfffcfcfc))
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                JobProgressBar(jobData)
                Spacer(modifier = Modifier.height(30.dp))
                Divider(
                    color = Color.LightGray, modifier = Modifier
                        .fillMaxWidth()
                        .width(1.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                var filteredJobs = JobsTabs(jobData)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(filteredJobs) { job ->
                        // Display job item here
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .border(0.2.dp, Color.Gray),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White, //Card background color
                                contentColor = Color.Black  //Card content color,e.g.text
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(100.dp)
                                    .padding(vertical = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        "#${job.jobNumber.toString()}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = subTitleTextColor,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        job.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        "${
                                            formatDateTimeRange(
                                                startDateTime = job.startTime,
                                                endDateTime = job.endTime
                                            )
                                        }",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = subTitleTextColor,
                                    )
                                }

                            }
                        }
                    }
                }

            }
        })
}

@Composable
fun JobProgressBar(jobData: List<JobApiModel>) {
    val totalCompletedJobs = getTotalCompletedJobs(jobData)

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${jobData.size.toString()} Jobs",
                style = MaterialTheme.typography.bodyMedium,
                color = subTitleTextColor,

                )
            Text(
                "${totalCompletedJobs} of ${jobData.size.toString()} completed",
                style = MaterialTheme.typography.bodyMedium,
                color = subTitleTextColor,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        JobProgressBarChart(jobs = jobData);

    }
}


@Composable
fun JobsTabs(jobs: List<JobApiModel>): List<JobApiModel> {
    var selectedTabIndex by remember {
        mutableStateOf(0)
    }

    var filteredJobs by remember { mutableStateOf<List<JobApiModel>>(emptyList()) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Set background color
    ) {
        Column {
            ScrollableTabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = selectedTabIndex,
                contentColor = Color.White, // Set text color
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Purple40
                    )
                }
            ) {
                JobStatus.values().forEachIndexed { index, status ->
                    Tab(
                        text = {

                            Text(
                                status.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black,
                                )

                        },
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            println("selection")
                            filteredJobs = jobs
                            print(filteredJobs.size)
                            filteredJobs = when (JobStatus.values()[selectedTabIndex]) {
                                JobStatus.YetToStart -> jobs.filter { it.status == JobStatus.YetToStart }
                                JobStatus.InProgress -> jobs.filter { it.status == JobStatus.InProgress }
                                JobStatus.Canceled -> jobs.filter { it.status == JobStatus.Canceled }
                                JobStatus.Completed -> jobs.filter { it.status == JobStatus.Completed }
                                JobStatus.Incomplete -> jobs.filter { it.status == JobStatus.Incomplete }
                            }
                        }
                    )
                }
            }
            filteredJobs = when (JobStatus.values()[selectedTabIndex]) {
                JobStatus.YetToStart -> jobs.filter { it.status == JobStatus.YetToStart }
                JobStatus.InProgress -> jobs.filter { it.status == JobStatus.InProgress }
                JobStatus.Canceled -> jobs.filter { it.status == JobStatus.Canceled }
                JobStatus.Completed -> jobs.filter { it.status == JobStatus.Completed }
                JobStatus.Incomplete -> jobs.filter { it.status == JobStatus.Incomplete }
            }


        }
    }

    return filteredJobs
}


@Composable
fun DisplayJobsByStatus(jobs: List<JobApiModel>) {
    LazyColumn {
        items(jobs) { job ->
            // Display job item here
            Text(job.title)
        }
    }
}

@Composable
fun formatDateTimeRange(startDateTime: String, endDateTime: String): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm a")

    val startInstant = Instant.parse(startDateTime)
    val startDate = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault())

    val endInstant = Instant.parse(endDateTime)
    val endDate = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault())

    val currentDate = LocalDateTime.now()

    val formattedStart = if (startDate.toLocalDate() == currentDate.toLocalDate()) {
        "Today ${startDate.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))}"
    } else {
        startDate.format(formatter)
    }

    val formattedEnd = endDate.format(formatter)

    return "$formattedStart - $formattedEnd"
}


@Composable
@Preview(showBackground = true)
fun DashboardPreview() {
    val navController = rememberNavController()
    AppTheme {
        JobsScreen(navController)
    }
}
