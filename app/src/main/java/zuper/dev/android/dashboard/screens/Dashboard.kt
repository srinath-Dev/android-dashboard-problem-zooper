package zuper.dev.android.dashboard.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import zuper.dev.android.dashboard.R
import zuper.dev.android.dashboard.data.model.InvoiceApiModel
import zuper.dev.android.dashboard.data.model.InvoiceStatus
import zuper.dev.android.dashboard.data.model.JobApiModel
import zuper.dev.android.dashboard.data.model.JobStatus
import zuper.dev.android.dashboard.data.remote.SampleData
import zuper.dev.android.dashboard.ui.theme.subTitleTextColor
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

@Composable
fun Dashboard(navController: NavHostController) {
    ScaffoldWithTopBar(navController)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopBar(navController: NavHostController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Dashboard")
                },
//                navigationIcon = {
//                    IconButton(onClick = {}) {
//                        Icon(Icons.Filled.ArrowBack, "backIcon")
//                    }
//                },
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
                GreetingCard()
                Spacer(modifier = Modifier.height(10.dp))
                JobStatsCard(navController)
                Spacer(modifier = Modifier.height(10.dp))
                InvoiceStatsCard()
            }
        })
}


@Composable
fun GreetingCard() {
    val currentDate = remember { LocalDate.now() }

    // Format the date in the desired format
    val formattedDate = remember(currentDate) {
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)
        currentDate.format(formatter)
    }

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
            contentColor = Color.Black  //Card content color, e.g., text
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(120.dp)// Center the Box both vertically and horizontally
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()  // Fill the available size within the Box
            ) {
                Column {
                    Text(
                        "Hello, Srinath A! 👋",
                        style = MaterialTheme.typography.headlineSmall
                            .copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Text(
                        formattedDate, style = MaterialTheme.typography.bodyMedium,
                        color = subTitleTextColor
                    )
                }

                ImageFromLocal()

            }
        }
    }
}

@Composable
fun ImageFromLocal() {
    // Load the image from the local resources
    val imagePainter = painterResource(id = R.drawable.srinath)  // Replace with your image resource

    // Display the image
    Image(
        painter = imagePainter,
        contentDescription = "Your Image",
        modifier = Modifier
            .size(120.dp)  // Adjust the size as needed

            .clip(RoundedCornerShape(5.dp))
    )
}

fun getTotalCompletedJobs(jobList: List<JobApiModel>): Int {
    return jobList.count { it.status == JobStatus.Completed }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobStatsCard(navController: NavHostController) {

    val jobData: List<JobApiModel> = SampleData.generateRandomJobList(20);
    val totalCompletedJobs = getTotalCompletedJobs(jobData)
    val progressData = calculateProgressSegments(jobData)

    Card(
        shape = RoundedCornerShape(8.dp),
        onClick = {
            navController.navigate("jobs")
        },
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
        ),

    ) {
        Box(
            modifier = Modifier
                .height(250.dp)
                .padding(vertical = 16.dp)
        ) {
            Column {
                Text("Job Stats",
                    style = MaterialTheme.typography.bodyLarge
                        .copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color.LightGray, modifier = Modifier
                    .fillMaxWidth()
                    .width(1.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${jobData.size.toString()} Jobs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = subTitleTextColor,

                        )
                    Text("${totalCompletedJobs} of ${jobData.size.toString()} completed",
                        style = MaterialTheme.typography.bodyMedium
                        ,
                        color = subTitleTextColor,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                JobProgressBarChart(jobs = jobData);

                Spacer(modifier = Modifier.height(10.dp))

                JobList(progressData)

            }

        }

    }
}

@Composable
fun JobListItem(progressSegment: ProgressSegment) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Small square with job color
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(progressSegment.color)
        )

        // Job name and job count text
        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
        ) {
            Text(text = "${progressSegment.status.name} (${progressSegment.count})",
                style = MaterialTheme.typography.bodySmall
                ,
                color = subTitleTextColor,)

        }
    }
}

@Composable
fun JobList(progressSegments: List<ProgressSegment>) {
    LazyVerticalGrid(
        GridCells.Fixed(2),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()

    ) {
        items(progressSegments) { segment ->
            JobListItem(segment)
        }
    }
}

data class ProgressSegment(val color: Color, val progress: Float, val status: JobStatus, val count: Int)

data class ProgressSegmentInvoice(val color: Color, val progress: Float, val status: InvoiceStatus, val count: Int)

@Composable
fun CustomProgressBar(progressSegments: List<ProgressSegment>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()

            .height(15.dp)
            .padding(horizontal = 16.dp)

    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .clip(RoundedCornerShape(16.dp))
        ) {
            val totalWidth = size.width
            var start = 0f

            progressSegments.forEachIndexed { index, segment ->
                // Calculate segment width
                val segmentWidth = if (index == progressSegments.size - 1) {
                    // Last segment takes up the remaining width
                    totalWidth - start
                } else {
                    totalWidth * segment.progress
                }

                // Draw segment
                drawRect(
                    color = segment.color,
                    size = size.copy(width = segmentWidth, height = size.height),
                    topLeft = Offset(start, 0f)
                )

                start += segmentWidth
            }

        }

    }
}

@Composable
fun JobProgressBarChart(jobs: List<JobApiModel>) {
    val progressSegments = calculateProgressSegments(jobs)

    println(jobs)

    CustomProgressBar(progressSegments)
}

@Composable
fun calculateProgressSegments(jobs: List<JobApiModel>): List<ProgressSegment> {
    val statusColors = mapOf(
        JobStatus.YetToStart to Color.Gray,
        JobStatus.InProgress to Color.Blue,
        JobStatus.Canceled to Color.Yellow,
        JobStatus.Incomplete to Color.Red,
        JobStatus.Completed to Color.Green,
    )

    val totalJobs = jobs.size
    val progressSegments = mutableListOf<ProgressSegment>()

    var start = 0f
    for (status in JobStatus.values()) {
        val count = jobs.count { it.status == status }
        val progress = count.toFloat() / totalJobs.toInt()
        Log.d("Progress", "$status: $progress")
        progressSegments.add(ProgressSegment(statusColors[status] ?: Color.Gray, start + progress, status,count))
        start += progress
    }

    return progressSegments
}


@Composable
fun InvoiceStatsCard() {
    val invoiceData: List<InvoiceApiModel> = SampleData.generateRandomInvoiceList(20);
    val totalPaid = invoiceData.count { it.status == InvoiceStatus.Paid }
    val progressData = calculateProgressSegmentsInvoice(invoiceData)
    val totalValue = invoiceData.sumBy { it.total }

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
                .height(250.dp)
                .padding(vertical = 16.dp)
        ) {
            Column {
                Text("Invoice Stats",
                    style = MaterialTheme.typography.bodyLarge
                        .copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color.LightGray, modifier = Modifier
                    .fillMaxWidth()
                    .width(1.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(" Total Value (${formatCurrency(totalValue)})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = subTitleTextColor,

                        )
                    Text("(${formatCurrency(totalPaid)}) collected",
                        style = MaterialTheme.typography.bodyMedium
                        ,
                        color = subTitleTextColor,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                InvoiceProgressBarChart(invoiceData);

                Spacer(modifier = Modifier.height(10.dp))

                InvoiceList(progressData)

            }

        }

    }
}

fun formatCurrency(amount: Int): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    currencyFormat.currency = Currency.getInstance("USD")

    return currencyFormat.format(amount.toLong())
}

@Composable
fun calculateProgressSegmentsInvoice(invoices: List<InvoiceApiModel>): List<ProgressSegmentInvoice> {
    val statusColors = mapOf(
        InvoiceStatus.Draft to Color.Gray,
        InvoiceStatus.Pending to Color.Yellow,
        InvoiceStatus.Paid to Color.Green,
        InvoiceStatus.BadDebt to Color.Red,
    )

    val totalInvoices = invoices.size
    val progressSegments = mutableListOf<ProgressSegmentInvoice>()

    var start = 0f
    for (status in InvoiceStatus.values()) {
        val count = invoices.count { it.status == status }
        val progress = (count.toFloat() / totalInvoices * 100).toInt() // Convert progress to int
        Log.d("Progress", "$status: $progress")
        progressSegments.add(ProgressSegmentInvoice(statusColors[status] ?: Color.Gray, start + progress / 100f, status, count))
        start += progress / 100f
    }

    return progressSegments
}

@Composable
fun InvoiceListItem(progressSegment: ProgressSegmentInvoice) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Small square with job color
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(progressSegment.color)
        )

        // Job name and job count text
        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
        ) {
            Text(text = "${progressSegment.status.name} (${progressSegment.count})",
                style = MaterialTheme.typography.bodySmall
                ,
                color = subTitleTextColor,)

        }
    }
}

@Composable
fun InvoiceList(progressSegments: List<ProgressSegmentInvoice>) {
    LazyVerticalGrid(
        GridCells.Fixed(2),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()

    ) {
        items(progressSegments) { segment ->
            InvoiceListItem(segment)
        }
    }
}



@Composable
fun CustomProgressBarInvoice(progressSegments: List<ProgressSegmentInvoice>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()

            .height(15.dp)
            .padding(horizontal = 16.dp)

    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .clip(RoundedCornerShape(16.dp))
        ) {
            val totalWidth = size.width
            var start = 0f

            progressSegments.forEachIndexed { index, segment ->
                // Calculate segment width
                val segmentWidth = if (index == progressSegments.size - 1) {
                    // Last segment takes up the remaining width
                    totalWidth - start
                } else {
                    totalWidth * segment.progress
                }

                // Draw segment
                drawRect(
                    color = segment.color,
                    size = size.copy(width = segmentWidth, height = size.height),
                    topLeft = Offset(start, 0f)
                )

                start += segmentWidth
            }

        }

    }
}

// Example usage:
@Composable
fun InvoiceProgressBarChart(invoices: List<InvoiceApiModel>) {
    val progressSegments = calculateProgressSegmentsInvoice(invoices)
    CustomProgressBarInvoice(progressSegments)
}
