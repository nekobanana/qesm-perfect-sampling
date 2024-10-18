import json
import os
from statistics import quantiles

import numpy as np
from matplotlib import pyplot as plt


def main(transient_json, results_json, i):
    with open(transient_json) as f:
        transient_data = json.load(f)
    with open(results_json) as f:
        ps_data = json.load(f)
    ps_steps = [entry['steps'] for entry in ps_data]
    steady_state_dist = get_vector_from_json_map(transient_data['steadyStateDistribution'])
    # transient_pi_n = get_vector_from_json_map(transient_data['transientAnalysis'])

    quantiles_range = np.arange(0.001, 1.001, 0.001)
    errors_range = 1 - quantiles_range

    transient_data_analysis = np.array(list(transient_data['transientAnalysis'].values()))
    transient_ss_distances = np.linalg.norm(steady_state_dist - transient_data_analysis, axis=1)
    transient_quantiles_times = np.array([np.argmax(transient_ss_distances <= error) for error in errors_range], dtype=float)
    tqt_no_zeros = np.trim_zeros(transient_quantiles_times, 'f')
    f_l = len(tqt_no_zeros)
    tqt_no_zeros = np.trim_zeros(tqt_no_zeros, 'b')
    for idx, t in enumerate(transient_quantiles_times):
        if t == 0 and transient_ss_distances[int(t)] > errors_range[idx]:
            transient_quantiles_times[idx] = np.nan
    ps_quantiles = np.quantile(ps_steps, quantiles_range)
    plt.plot(ps_quantiles, transient_quantiles_times)
    ps_quantiles_no_zeros = ps_quantiles[len(transient_quantiles_times)-f_l:len(transient_quantiles_times)-f_l+len(tqt_no_zeros)]
    coef = np.polyfit(ps_quantiles_no_zeros, tqt_no_zeros, 1)
    poly1d_fn = np.poly1d(coef)
    plt.plot(ps_quantiles_no_zeros, poly1d_fn(ps_quantiles_no_zeros), '--k', label=coef)
    plt.legend(loc='best')
    # plt.title('DTMC')
    plt.xlabel('Perfect sampling steps')
    plt.ylabel('Transient analysis time')
    plt.savefig(f'results/{i}/transient.png')
    plt.close()
    derivative_y = np.diff(transient_quantiles_times) / np.diff(ps_quantiles)
    derivative_x = (ps_quantiles[:-1] + ps_quantiles[1:]) / 2
    # plt.plot(derivative_x, derivative_y)
    # plt.savefig(f'results/{i}/transient_derivative.png')
    # plt.close()

def get_vector_from_json_map(json_map):
    return np.array([json_map.get(str(state), 0)
                     for state in range(0, max([int(s) for s in json_map.keys()]) + 1)])


if __name__ == '__main__':
    for i in range(27):
        try:
            main(f'results/{i}/transient.json', f'results/{i}/results.json', i)

        except Exception as err:
            print(Exception, err)