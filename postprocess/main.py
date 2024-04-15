import json

import matplotlib.pyplot as plt
import numpy as np


def main(sequence_json, results_json, cftp=True):
    plt.figure()
    data = json.load(open(sequence_json))
    n = data['P']['rows']
    for s in data['sequence']:
        time = s['time']
        plt.plot([time, time], [0, n-1], '--', c='gray', linewidth=0.5)
        for stateId, state in s['states'].items():
            plt.scatter(time, stateId, c='gray')
            if state['nextStateId'] is not None:
                plt.plot([time, time + 1], [stateId, state['nextStateId']], c='black', linewidth=0.7)

    if cftp:
        colormap = plt.colormaps['Set2'].colors
        color = iter(colormap)
        for stateId in range(n):
            c = next(color)
            prev_stateId = stateId
            for s in reversed(data['sequence']):
                state = s['states'][f'{prev_stateId}']
                time = s['time']
                if state['nextStateId'] is not None:
                    plt.plot([time, time + 1], [prev_stateId, state['nextStateId']], c=c)
                    prev_stateId = state['nextStateId']
    plt.savefig(f'sequence_{"cftp" if cftp else "forward"}.png')

    plt.figure()
    results = json.load(open(results_json))
    steps = [r['steps'] for r in results]
    counts, bins = np.histogram(steps, bins=np.arange(0, max(steps)))
    plt.xticks(np.arange(0, max(steps), 2))
    plt.hist(bins[:-1], bins, weights=counts)
    plt.savefig(f'hist_{"cftp" if cftp else "forward"}.png')

if __name__ == '__main__':
    main('output_seq.json', 'results.json')
    main('output_seq_f.json', 'results_f.json', False)