import json
import os
import traceback
from collections import defaultdict

import numpy as np
import pandas as pd
from matplotlib import pyplot as plt

def main(transient_json, results_json, i, N):
    with open(transient_json) as f:
        transient_data = json.load(f)
    with open(results_json) as f:
        ps_data = json.load(f)
    ps_steps = [entry['steps'] for entry in ps_data]
    steady_state_dist = get_vector_from_json_map(transient_data['steadyStateDistribution'])
    # N = steady_state_dist.shape[0]
    # m = 16
    # transient_pi_n = get_vector_from_json_map(transient_data['transientAnalysis'])
    step = 0.00001
    quantiles_range = np.arange(step, 1+step, step)
    errors_range = (1 - quantiles_range)

    transient_data_analysis = np.array(list(transient_data['transientAnalysis'].values()))
    transient_ss_distances = np.linalg.norm(steady_state_dist - transient_data_analysis, axis=1) / N
    transient_quantiles_times = np.array([np.argmax(transient_ss_distances <= error) for error in errors_range], dtype=float)
    tqt_no_zeros = np.trim_zeros(transient_quantiles_times, 'f')
    f_l = len(tqt_no_zeros)
    tqt_no_zeros = np.trim_zeros(tqt_no_zeros, 'b')
    for idx, t in enumerate(transient_quantiles_times):
        if t == 0 and transient_ss_distances[int(t)] > errors_range[idx]:
            transient_quantiles_times[idx] = np.nan
    ps_quantiles = np.quantile(ps_steps, quantiles_range)
    ps_quantiles_no_zeros = ps_quantiles[len(transient_quantiles_times)-f_l:len(transient_quantiles_times)-f_l+len(tqt_no_zeros)]
    coef = np.polyfit(ps_quantiles_no_zeros, tqt_no_zeros, 1)
    poly1d_fn = np.poly1d(coef)
    first_q_no_zero = quantiles_range[len(transient_quantiles_times)-f_l]
    first_s_no_zero = ps_quantiles[len(transient_quantiles_times)-f_l]
    label_blue=f'q={first_q_no_zero}, s={first_s_no_zero}'
    label_line=f'{coef}'
    plt.plot(ps_quantiles, transient_quantiles_times, label=label_blue)
    plt.plot(ps_quantiles_no_zeros, poly1d_fn(ps_quantiles_no_zeros), '--k', label=label_line)
    plt.legend(loc='best')
    # plt.title('DTMC')
    plt.xlabel('Perfect sampling steps')
    plt.ylabel('Transient analysis time')
    plt.savefig(f'results/{i}/transient.png')
    plt.close()
    plt.plot(ps_quantiles_no_zeros, tqt_no_zeros, label=label_blue)
    plt.plot(ps_quantiles_no_zeros, poly1d_fn(ps_quantiles_no_zeros), '--k', label=label_line)
    plt.legend(loc='best')
    # plt.title('DTMC')
    plt.xlabel('Perfect sampling steps')
    plt.ylabel('Transient analysis time')
    plt.savefig(f'results/{i}/transient_no_zeros.png')
    plt.close()
    return coef
    derivative_y = np.diff(transient_quantiles_times) / np.diff(ps_quantiles)
    derivative_x = (ps_quantiles[:-1] + ps_quantiles[1:]) / 2
    # plt.plot(derivative_x, derivative_y)
    # plt.savefig(f'results/{i}/transient_derivative.png')
    # plt.close()

def get_vector_from_json_map(json_map):
    return np.array([json_map.get(str(state), 0)
                     for state in range(0, max([int(s) for s in json_map.keys()]) + 1)])


if __name__ == '__main__':
    java_results_dir = '../files/output'
    results = defaultdict(lambda: [])
    for output_file in os.listdir(java_results_dir):
        i = output_file.split('.')[0]
        try:
            output_json = json.load(open(os.path.join(java_results_dir, output_file)))
            coef = main(f'results/{i}/transient.json', f'results/{i}/results.json', i, output_json['config']['dtmcGeneratorConfig']['n'])
            output_file_path = os.path.join(java_results_dir, output_file)
            results['file-name'].append(output_json['fileName'])
            results['N'].append(output_json['config']['dtmcGeneratorConfig']['n'])
            results['edges-number'].append(output_json['config']['dtmcGeneratorConfig']['edgesNumberDistribution']['n'])
            loc_min = output_json['config']['dtmcGeneratorConfig']['edgesLocalityDistribution']['min']
            loc_max = output_json['config']['dtmcGeneratorConfig']['edgesLocalityDistribution']['max']
            results['locality-min'].append(loc_min)
            results['locality-max'].append(loc_max)
            results['locality'].append(f'[{loc_min}\\comma {loc_max}]')
            results['linear-coeff-0'].append(coef[0])
            results['linear-coeff-1'].append(coef[1])
            df = pd.DataFrame.from_dict(results)
            df = df.sort_values(by=['N', 'edges-number', 'locality-max'])
            df.to_csv('../tables/transient_ps.csv', index=False)

        except Exception as err:
            print(traceback.format_exc())
