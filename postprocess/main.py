import getopt
import json
import os
import sys

import matplotlib.pyplot as plt

from analysis2 import histogram


def sequence_diagram(sequence_json):
    plt.figure(figsize=(len(sequence_json)*2, 6), dpi=200)
    parent_dir = os.path.dirname(os.path.join(sequence_json))
    data = json.load(open(sequence_json))
    n = data['P']['rows']
    for t, s in data['sequence'].items():
        time = s['time']
        plt.plot([time, time], [0, n-1], '--', c='gray', linewidth=0.5)
        for stateId, state in s['states'].items():
            plt.scatter(time, stateId, c='gray')
            if state['nextStateId'] is not None:
                plt.plot([time, time + 1], [stateId, state['nextStateId']], c='black', linewidth=0.7)

    color = ColormapIterator(plt.colormaps['Set2'].colors)
    for stateId in range(n):
        c = next(color)
        prev_state_id = stateId
        for t, s in reversed(data['sequence'].items()):
            state = s['states'][f'{prev_state_id}']
            time = s['time']
            if state['nextStateId'] is not None:
                plt.plot([time, time + 1], [prev_state_id, state['nextStateId']], c=c)
                prev_state_id = state['nextStateId']
    plt.savefig(f'{parent_dir}/sequence_cftp.png')

def main(argument_list):
    options = "h:s:"
    long_options = ["histogram", "sequence"]
    try:
        arguments, values = getopt.getopt(argument_list, options, long_options)
        for current_argument, current_value in arguments:
            if current_argument in ("-h", "--histogram"):
                print('Generating histogram...')
                histogram(current_value)
            elif current_argument in ("-s", "--sequence"):
                print('Generating sequence image...')
                sequence_diagram(current_value)
    except getopt.error as err:
        print(str(err))


class ColormapIterator:
    def __init__(self, cmap):
        self._cmap = cmap
        self.cmap = iter(cmap[:])

    def __iter__(self):
        while True:
            try:
                yield next(self.cmap)
            except StopIteration:
                self.cmap = iter(self._cmap[:])

    def __next__(self):
        return next(self.__iter__())


if __name__ == '__main__':

    main(sys.argv[1:])
    