import json
import matplotlib.pyplot as plt
import numpy as np
import scipy.stats as stats

def main1():
    # Load the JSON data
    with open('results/output/results.json') as f:
        data = json.load(f)

    # Extract the 'steps' values
    steps = [entry['steps'] for entry in data]
    max_bins = np.arange(0, max(steps))

    # Plot the histogram
    plt.hist(steps, bins=max_bins, density=False, alpha=0.6, color='g')

    # Add title and labels
    plt.title('Histogram of Steps')
    plt.xlabel('Steps')
    plt.ylabel('Density')

    # Show the plot
    plt.show()

    # Calculate descriptive statistics
    mean_steps = np.mean(steps)
    median_steps = np.median(steps)
    std_steps = np.std(steps)
    skewness_steps = stats.skew(steps)
    kurtosis_steps = stats.kurtosis(steps)

    plt.figure()

    exp_params = stats.expon.fit(steps)
    lognorm_params = stats.lognorm.fit(steps, floc=0)
    gamma_params = stats.gamma.fit(steps, floc=0)

    # Plot the histogram with the fitted distributions
    x = np.linspace(min(steps), max(steps), 1000)
    pdf_expon = stats.expon.pdf(x, *exp_params) * len(steps)
    pdf_lognorm = stats.lognorm.pdf(x, *lognorm_params) * len(steps)
    pdf_gamma = stats.gamma.pdf(x, *gamma_params) * len(steps)

    plt.hist(steps, bins=max_bins, density=False, alpha=0.6, color='g', label='Data')
    plt.plot(x, pdf_expon, 'r-', label='Exponential')
    plt.plot(x, pdf_lognorm, 'b-', label='Log-Normal')
    plt.plot(x, pdf_gamma, 'k-', label='Gamma')

    # Add title and labels
    plt.title('Fitted Distributions to Steps Data')
    plt.xlabel('Steps')
    plt.ylabel('Density')
    plt.legend()
    plt.show()

    # Calculate goodness of fit using the Kolmogorov-Smirnov test
    # Perform KS tests
    ks_stat_exp, p_value_exp = stats.kstest(steps, 'expon', args=exp_params)
    ks_stat_lognorm, p_value_lognorm = stats.kstest(steps, 'lognorm', args=lognorm_params)
    ks_stat_gamma, p_value_gamma = stats.kstest(steps, 'gamma', args=gamma_params)
    print(f"Exponential distribution: KS-statistic = {ks_stat_exp}, p-value = {p_value_exp}")
    print(f"Log-Normal distribution: KS-statistic = {ks_stat_lognorm}, p-value = {p_value_lognorm}")
    print(f"Gamma distribution: KS-statistic = {ks_stat_gamma}, p-value = {p_value_gamma}")

    plt.figure()
    # observed_freq, bin_edges = np.histogram(steps, bins=int(np.sqrt(len(steps))))
    b = max(steps) - min(steps)
    observed_freq, bin_edges = np.histogram(np.array(steps), bins=b)
    # x = np.linspace(min(steps), max(steps), b)
    plt.bar(bin_edges[:-1], observed_freq / len(steps), width=np.diff(bin_edges), align="edge", alpha=0.6, color='g')
    plt.show()
    # Perform chi-square tests
    # chi_square_exp, p_value_exp_chi = stats.chisquare(observed_freq, pdf_expon)
    # chi_square_lognorm, p_value_lognorm_chi = stats.chisquare(observed_freq, pdf_lognorm)
    # chi_square_gamma, p_value_gamma_chi = stats.chisquare(observed_freq, pdf_gamma)
    chi_square_exp, p_value_exp_chi = chi_square_test(steps, 'expon', exp_params, observed_freq, bin_edges)
    chi_square_lognorm, p_value_lognorm_chi = chi_square_test(steps, 'lognorm', lognorm_params, observed_freq, bin_edges)
    chi_square_gamma, p_value_gamma_chi = chi_square_test(steps, 'gamma', gamma_params, observed_freq, bin_edges)
    print(f'chi_square_exp: Stat = {chi_square_exp}, p-value = {p_value_exp_chi}')
    print(f'chi_square_lognorm: Stat = {chi_square_lognorm}, p-value = {p_value_lognorm_chi}')
    print(f'chi_square_gamma: Stat = {chi_square_gamma}, p-value = {p_value_gamma_chi}')
    # ad_exp = stats.anderson(steps, dist='expon')
    # ad_lognorm = stats.anderson(steps, dist='lognorm')
    # ad_gamma = stats.anderson(steps, dist='gamma')
    # print(f'ad_exp: {ad_exp}')
    # print(f'ad_lognorm: {ad_lognorm}')
    # print(f'ad_gamma: {ad_gamma}')

# Function to compute the chi-square test
def chi_square_test(data, dist_name, params, observed_freq, bin_edges):

    # Compute expected frequencies
    cdf = getattr(stats, dist_name).cdf(bin_edges, *params)
    expected_freq = len(data) * np.diff(cdf)
    # Chi-square test
    chi_square_stat = ((observed_freq - expected_freq) ** 2 / expected_freq).sum()
    p_value = stats.chi2.sf(chi_square_stat, df=len(observed_freq) - 1 - len(params))
    return chi_square_stat, p_value

if __name__ == '__main__':
    main1()
