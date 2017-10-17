# load required libraries (install if not existent)
if (! require(sfsmisc)) {
    install.packages("sfsmisc", repos = 'http://cran.us.r-project.org')
    library(sfsmisc)
}else {
    library(sfsmisc)
}

# accept command line arguments
args <- commandArgs(TRUE)

# model parameters
N <- as.numeric(args[1])
NS <- as.numeric(args[2])
GS <- as.numeric(args[3])
rmsd_from <- as.numeric(args[4])
rmsd_to <- as.numeric(args[5])

# sample size
sample_size <- as.numeric(args[6])

# file names
input_file <- args[7]
output_file <- args[8]

print("program arguments")
print(args)

# calculate point-weight, pwt
# N...quantity of targets in reference population
# NS...quantity of eliminated matches in sample population
# GS...quantity of geometric matches in sample population
calc_pwt <- function(N, NS, GS){

    UB <- (NS / (NS + GS)) * N + NS / (NS + GS)

    pwt_result <- floor(UB) / N

    return(pwt_result)
}

# calculate p-value of a single geometric match with RMSD x
# d...density function
# x...match rmsd
# rmsd_cutoff...geometric rmsd cutoff
# pwt...point-weight
pv <- function(d, x, rmsd_min, rmsd_cutoff, pwt){

    # return 0 for exact match
    if (x == rmsd_min) {

        return(0);
    }

    # cumulative distribution function F
    # calculate integral
    A <- (1 - pwt) * integrate.xy(d$x, d$y, rmsd_min, x)
    B <- (1 - pwt) * integrate.xy(d$x, d$y, x, rmsd_cutoff)
    C <- pwt

    # p-value calculation
    pv_result <- A / (A + B + C)

    return(pv_result)
}

# read data
cat("Reading data...\n")
data <- read.csv(input_file, sep = ",", header = T)

# get RMSD values
rmsd <- data$RMSD

# sampling data
cat("Sampling data...\n")
rmsd_sampled <- rmsd;
if (length(rmsd) > 100000) {
    rmsd_sampled = sample(rmsd, sample_size)
}

# calculate density
cat("Calculating density estimation...\n")
d <- density(rmsd_sampled, kernel = "gaussian", bw = "SJ", from = rmsd_from, to = rmsd_to)

# calculate point-weight
cat("Calculating point-weight...\n")
pwt <- calc_pwt(N, NS, GS)

# calculate p-values
cat("Calculating p-values...\n")
pv_results <- vector("numeric", length(1 : length(rmsd)))
for (i in 1 : length(rmsd)) {
    pv_results[i] <- pv(d, rmsd[i], rmsd_from, rmsd_to, pwt)
}

# cbind data
#cat("Binding data...\n")
#final_data <- cbind.data.frame(data$RMSD,pv_results,data$PDB.ID,data$occ,data$seq,data$motif,data$title)

# writing data
cat("Writing data...\n")
#write.table(final_data, file=output_file,row.names=F, col.names=c("RMSD","p-value","PDB-ID","occ","seq","motif","title"), sep=',')
write.table(pv_results, file = output_file, row.names = F, col.names = F);

cat("Finished.\n")