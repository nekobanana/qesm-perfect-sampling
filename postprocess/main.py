import json

import matplotlib.pyplot as plt

data = json.load(open('output.json'))
n = data['P']['rows']
for s in data['sequence']:
    time = s['time']
    plt.plot([time, time], [0, n-1], '--', c='gray', linewidth=0.5)
    for stateId, state in s['states'].items():
        plt.scatter(time, stateId, c='gray')
        if state['nextStateId'] is not None:
            plt.plot([time, time + 1], [stateId, state['nextStateId']], c='black', linewidth=0.7)

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
plt.savefig('output.png')