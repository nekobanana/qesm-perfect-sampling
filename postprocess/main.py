import argparse
import getopt
import json
import os
import sys

import matplotlib.pyplot as plt
import numpy as np


def sequence_diagram(sequence_json, cftp=True):
# def sequence_diagram(sequence_json, results_json, cftp=True):
    plt.figure()
    parent_dir = os.path.dirname(os.path.join(sequence_json))
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
    plt.savefig(f'{parent_dir}/sequence_{"cftp" if cftp else "forward"}.png')


def results_histogram(results_json, cftp=True, num_labels=10):
    plt.figure()
    parent_dir = os.path.dirname(os.path.join(results_json))
    with open(results_json) as f:
        results = json.load(f)
    steps = [r['steps'] for r in results]
    counts, bins = np.histogram(steps, bins=np.arange(0, max(steps) + 1))
    max_steps = max(steps)
    step_interval = max_steps // num_labels if max_steps > num_labels else 1
    plt.xticks(np.arange(0, max_steps + 1, step_interval))
    plt.hist(bins[:-1], bins, weights=counts)
    plt.xlabel('Steps number')
    plt.ylabel('Frequency')
    plt.title('Steps number')

    # Salvare l'immagine
    plt.savefig(f'{parent_dir}/hist_{"cftp" if cftp else "forward"}.png')
    plt.close()
# def results_histogram(results_json, cftp=True):
#     plt.figure()
#     parent_dir = os.path.dirname(os.path.join(results_json))
#     results = json.load(open(results_json))
#     steps = [r['steps'] for r in results]
#     counts, bins = np.histogram(steps, bins=np.arange(0, max(steps)))
#     plt.xticks(np.arange(0, max(steps), 2))
#     plt.hist(bins[:-1], bins, weights=counts)
#     plt.savefig(f'{parent_dir}/hist_{"cftp" if cftp else "forward"}.png')

def main(argumentList):
    options = "h:s:"
    long_options = ["histogram", "sequence"]
    try:
        arguments, values = getopt.getopt(argumentList, options, long_options)
        for currentArgument, currentValue in arguments:
            if currentArgument in ("-h", "--histogram"):
                results_histogram(currentValue)
            elif currentArgument in ("-s", "--sequence"):
                sequence_diagram(currentValue)
    except getopt.error as err:
        print(str(err))


if __name__ == '__main__':

    main(sys.argv[1:])
