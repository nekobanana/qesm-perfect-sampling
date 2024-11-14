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


def main(results, names):
    experiment_name = os.path.basename(os.path.dirname(results[0]))
    # assert experiment_name == os.path.basename(os.path.dirname(results_json_2))
    histograms, steps = [], []
    for result, name in zip(results, names):
        histogram, s = get_histogram(result)
        histograms.append(histogram)
        steps.append(s)
    min_value = min(*[h[1][0] for h in histograms])
    max_value = max(*[h[1][-1] for h in histograms])
    # min_value = min(histogram1[1][0], histogram2[1][0])
    # max_value = max(histogram1[1][-1], histogram2[1][-1])
    for i in range(len(histograms)):
        histograms[i] = extend_histogram(histograms[i][0], histograms[i][1], min_value, max_value)
    # histogram1 = extend_histogram(histogram1[0], histogram1[1], min_value, max_value)
    # histogram2 = extend_histogram(histogram2[0], histogram2[1], min_value, max_value)
    distances = []
    for hist in histograms:
        distances.append(hist[0] / np.linalg.norm(hist))
    # dist1 = histogram1[0] / np.linalg.norm(histogram1)
    plot_dists(steps, names, f'{base_dir}/freq/{experiment_name}', False)
    plot_dists(steps, names, f'{base_dir}/pdf/{experiment_name}', True)
    plot_CDFs(steps, names, f'{base_dir}/cdf/{experiment_name}')

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
    base_dir = 'jensen_shannon/test'
    # base_dir = 'jensen_shannon/0.1_error_same_DTMC'
    Path(base_dir, 'freq').mkdir(exist_ok=True)
    Path(base_dir, 'pdf').mkdir(exist_ok=True)
    Path(base_dir, 'cdf').mkdir(exist_ok=True)
    Path(base_dir, 'divergence').mkdir(exist_ok=True)
    for n in range(0, 1):
        try:
            # results_json_1 = f'../results_history/same_DTMC/20241001-225331-single-random-0.01/results/{n}/results.json'
            # results_json_2 = f'../results_history/same_DTMC/20241001-004925-N-random-0.01/results/{n}/results.json'
            results_json_1 = f'results/{n}/results.json'
            results_json_3 = f'results/{n}/results_forward.json'
            desc1 = f'1 random'
            desc2 = f'N random'
            desc3 = f'1 random forward'
            main([results_json_1, results_json_3], [desc1, desc3])
        except Exception as e:
            print(traceback.format_exc())
