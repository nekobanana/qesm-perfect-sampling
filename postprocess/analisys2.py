import json
import numpy as np
import scipy.stats as stats
import matplotlib.pyplot as plt
import seaborn as sns


def main():
    # Load the data
    with open("results/output/results.json") as f:
        data = json.load(f)

    # Extract the 'steps' values
    steps = [entry['steps'] for entry in data]

    # Fit various distributions and perform goodness-of-fit tests
    distributions = ['norm', 'expon', 'gamma', 'lognorm', 'beta', 'weibull_min']
    results = []

    for dist_name in distributions:
        dist = getattr(stats, dist_name)
        param = dist.fit(steps)

        # Kolmogorov-Smirnov test
        D, p_value = stats.kstest(steps, dist_name, args=param)
        results.append((dist_name, D, p_value, param))

    # Find the best fit based on the highest p-value
    best_fit = max(results, key=lambda x: x[2])
    best_dist_name, best_D, best_p_value, best_param = best_fit

    # Plotting the histogram of the data and the best fit distribution
    plt.figure(figsize=(10, 6))
    plt.hist(steps, bins=max(steps) - min(steps) + 1, density=True, alpha=0.6, color='g', label='Data')
    plt.show()

    plt.figure(figsize=(10, 6))
    x = np.linspace(min(steps), max(steps), 1000)
    observed_freq, bin_edges = np.histogram(np.array(steps), bins=max(steps) - min(steps) + 1)
    # x = np.linspace(min(steps), max(steps), b)
    plt.bar(bin_edges[:-1], observed_freq / len(steps), width=np.diff(bin_edges), align="edge", alpha=0.3, color='grey')
    colormap = plt.colormaps['Set2'].colors
    color = iter(colormap)
    for dist in results:
        pdf = getattr(stats, dist[0]).pdf(x, *dist[3])
        plt.plot(x, pdf, '-', c=next(color), label=f'{dist[0]}' + (' (best fit)' if dist[0] == best_dist_name else ''))
    # Generate data from the best fit distribution
    # pdf_fitted = getattr(stats, best_dist_name).pdf(x, *best_param)

    # plt.plot(x, pdf_fitted, 'r-', label=f'Best fit: {best_dist_name}')
    plt.legend()
    plt.ylim([0, 1.05 * max(observed_freq) / len(steps)])
    plt.xlabel('Steps')
    plt.ylabel('Density')
    plt.title('Histogram of Steps with Best Fit Distribution')
    plt.show()

    print(f'Best Fit Distribution: {best_dist_name}')
    for dist in results:
        print(f'{dist[0]}:\t D={dist[1]},\t p={dist[2]}')


if __name__ == '__main__':
    main()
