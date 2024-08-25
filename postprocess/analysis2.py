import json
import math
import os

import numpy as np
import scipy.stats as stats
import matplotlib.pyplot as plt

def histogram(results_json):
    # Load the data
    with open(results_json) as f:
        data = json.load(f)
    parent_dir = os.path.dirname(os.path.join(results_json))
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
        results.append({
            'name': dist_name,
            'D': D,
            'p-value': p_value,
            'parameters': param
        })

    # Find the best fit based on the highest p-value
    best_fit = max(results, key=lambda x: x['p-value'])
    # best_dist_name, best_D, best_p_value, best_param = best_fit

    for b in [max(steps) - min(steps) + 1, 20]:
        plt.figure(figsize=(10, 6))
        plt.hist(steps, bins=b, density=False, alpha=0.6, color='g', label='Data')
        plt.savefig(f'{parent_dir}/hist_{b}.png')

    # Plotting the histogram of the data and the best fit distribution
    for b in [max(steps) - min(steps) + 1, 20]:
        plt.figure(figsize=(10, 6))
        x = np.linspace(min(steps), max(steps), 1000)
        observed_freq, bin_edges = np.histogram(np.array(steps), bins=b)
        plt.bar(bin_edges[:-1], observed_freq / len(steps), width=np.diff(bin_edges), align="edge", alpha=0.3, color='grey')
        colormap = plt.colormaps['Set2'].colors
        color = iter(colormap)
        for dist in results:
            pdf = getattr(stats, dist['name']).pdf(x, *dist['parameters'])
            plt.plot(x, pdf, '-', c=next(color), label=f'{dist["name"]}' + (' (best fit)' if dist["name"] == best_fit["name"] else ''))
        plt.legend()
        plt.ylim([0, 1.05 * max(observed_freq) / len(steps)])
        plt.xlabel('Steps')
        plt.ylabel('Density')
        plt.title('Histogram of Steps with Best Fit Distribution')
        plt.savefig(f'{parent_dir}/hist_fit_{b}.png')

    print(f'Best Fit Distribution: {best_fit["name"]}')
    for dist in results:
        print(f'{dist["name"]}:\t D={dist["D"]},\t p={dist["p-value"]}')

    r = {
        'best_fit': best_fit["name"],
        'results': results
    }
    r_path = os.path.join(parent_dir, 'best_fit.json')
    with open(r_path, 'w') as fp:
        json.dump(r, fp)
    # r_path_csv = os.path.join(parent_dir, 'best_fit.csv')


def gaussian(results_json):
    # Load the data
    with open(results_json) as f:
        data = json.load(f)
    parent_dir = os.path.dirname(os.path.join(results_json))
    steps = [entry['steps'] for entry in data]
    log_steps = [math.log(s) for s in steps]
    norm = getattr(stats, 'norm')
    param = norm.fit(log_steps)
    D, p_value = stats.kstest(log_steps, 'norm', args=param)

    # b = int(max(steps) - min(steps) + 1)
    plt.figure(figsize=(10, 6))
    plt.hist(log_steps, density=True, bins=50, alpha=0.6, label='Data')
    x = np.linspace(min(log_steps), max(log_steps), 1000)
    pdf = norm.pdf(x, *param)
    plt.plot(x, pdf, '-', linewidth=3, label='normal')
    plt.show()
    print(f'normal:\t D={D},\t p={p_value}')

if __name__ == '__main__':
    histogram('results/output26/results.json')
    # gaussian('results/output22/results.json')
