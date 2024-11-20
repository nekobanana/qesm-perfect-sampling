import itertools
import json
import os
import traceback
from pathlib import Path

import numpy as np
from matplotlib import pyplot as plt
from scipy.spatial import distance

def get_histogram(results_json):
    with open(results_json) as f:
        data = json.load(f)
    steps = [entry['steps'] for entry in data]
    bins = max(steps) - min(steps)
    observed_freq, bin_edges = np.histogram(np.array(steps), bins=bins)
    return (observed_freq, bin_edges), steps

def extend_histogram(observed_freq, bin_edges, min_value, max_value):
    new_observed_freq = []
    new_bin_edges = []
    hist_idx = 0
    for n in range(int(min_value), int(max_value) + 1):
        if hist_idx < len(observed_freq) and bin_edges[hist_idx] == n:
            new_observed_freq.append(observed_freq[hist_idx])
            new_bin_edges.append(n)
            hist_idx += 1
        else:
            new_observed_freq.append(0)
            new_bin_edges.append(n)
    return new_observed_freq, new_bin_edges


def main(results, names, quantiles = None, doJS = False):
    if quantiles is None:
        quantiles = []
    experiment_name = os.path.basename(os.path.dirname(results[0]))
    histograms, steps = [], []
    avg_data = []
    for result, name in zip(results, names):
        histogram, s = get_histogram(result)
        histograms.append(histogram)
        steps.append(s)
        avg_data.append({
            "name": name,
            "mean": np.mean(s),
            "std": np.std(s),
            "cv": np.std(s) / np.mean(s),
            "quantiles": {}
        })
        quantile_values = np.quantile(s, quantiles)
        for k, v in zip(quantiles, quantile_values):
            avg_data[-1]['quantiles'][k] = v

    with open(f'{base_dir}/distributions/{experiment_name}.json', 'w') as fp:
            json.dump(avg_data, fp, indent=4)
    min_value = min(*[h[1][0] for h in histograms])
    max_value = max(*[h[1][-1] for h in histograms])
    for i in range(len(histograms)):
        histograms[i] = extend_histogram(histograms[i][0], histograms[i][1], min_value, max_value)
    distances = []
    for hist in histograms:
        distances.append(hist[0] / np.linalg.norm(hist))
    plot_dists(steps, names, f'{base_dir}/freq/{experiment_name}', False)
    plot_dists(steps, names, f'{base_dir}/pdf/{experiment_name}', True)
    plot_CDFs(steps, names, f'{base_dir}/cdf/{experiment_name}')

    if doJS:
        summary_pairs = []
        for (path1, name1, dist1), (path2, name2, dist2) in itertools.combinations(zip(results, names, distances), 2):
            div_js = distance.jensenshannon(dist1, dist2)
            print(f'Jensen-Shannon divergence: {div_js}')
            summary_pairs.append({
                'distributions': [
                    {
                        'path': path1,
                        'description': name1
                    },
                    {
                        'path': path2,
                        'description': name2
                    }
                ],
                'jensen-shannon-divergence': div_js
            })
        summary = {
            'graph': experiment_name,
            'comparisons': []
        }
        for pair in summary_pairs:
            summary['comparisons'].append(pair)
        with open(f'{base_dir}/divergence/{experiment_name}.json', 'w') as fp:
            json.dump(summary, fp, indent=4)

def plot_dists(steps_dists, names, image_name, normalized=False):
    plt.figure(figsize=(10, 6))
    for steps, name in zip(steps_dists, names):
        plt.hist(steps, bins=max(steps)-min(steps), density=normalized, alpha=0.6, label=name)
        plt.legend(loc="upper right")
        plt.xlabel('Steps')
        plt.ylabel('pdf')
        plt.title('Comparison between experiments using 1 random value and N random values')
    plt.savefig(f'{image_name}_pdf.png')
    plt.close()

def plot_CDFs(steps_dists, names, image_name):
    plt.figure(figsize=(10, 6))
    for steps, name in zip(steps_dists, names):
        plt.ecdf(steps, label=name)
        plt.legend(loc="upper right")
        plt.xlabel('Steps')
        plt.ylabel('CDF')
        plt.title('Comparison between experiments using 1 random value and N random values')
    plt.savefig(f'{image_name}_CDF.png')
    plt.close()


if __name__ == '__main__':
    base_dir = 'jensen_shannon/same_DTMC_2_coupling'
    # base_dir = 'jensen_shannon/0.1_error_same_DTMC'
    Path(base_dir).mkdir(exist_ok=True)
    Path(base_dir, 'freq').mkdir(exist_ok=True)
    Path(base_dir, 'pdf').mkdir(exist_ok=True)
    Path(base_dir, 'cdf').mkdir(exist_ok=True)
    Path(base_dir, 'divergence').mkdir(exist_ok=True)
    Path(base_dir, 'distributions').mkdir(exist_ok=True)
    for n in range(0, 28):
        try:
            results_json_1 = f'../results_history/same_DTMC_2/20241115-090238-single-random/results/{n}/results.json'
            results_json_2 = f'../results_history/same_DTMC_2/20241115-090342-N-random/results/{n}/results.json'
            results_json_3 = f'../results_history/same_DTMC_2/20241119-180022-forward-coupling/results/{n}/results_forward_coupling.json'
            # results_json_3 = f'../results_history/same_DTMC_2/20241115-090238-single-random/results/{n}/results_forward.json'
            # results_json_4 = f'../results_history/same_DTMC_2/20241115-090342-N-random/results/{n}/results_forward.json'

            # results_json_1 = f'results/{n}/results.json'
            # results_json_3 = f'results/{n}/results_forward.json'
            desc1 = f'1 random'
            desc2 = f'N random'
            desc3 = f'forward coupling'
            # desc3 = f'1 random forward'
            # desc4 = f'N random forward'
            main([results_json_1, results_json_2, results_json_3],
                 [desc1, desc2, desc3], [0.8, 0.85, 0.9, 0.95] , False)
        except Exception as e:
            print(traceback.format_exc())
