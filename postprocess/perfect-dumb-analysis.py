import json
import math
import os
from collections import defaultdict

import numpy as np
import pandas as pd
import scipy.stats as stats
import matplotlib.pyplot as plt

def main():
    results = defaultdict(lambda: [])
    for output_file in os.listdir('../files/output'):
        output_file_path = os.path.join('../files/output', output_file)
        output_json = json.load(open(output_file_path))
        results['file-name'].append(output_json['fileName'])
        results['N'].append(output_json['config']['n'])
        results['edges-number'].append(output_json['config']['edgesNumberDistribution']['n'])
        loc_min = output_json['config']['edgesLocalityDistribution']['min']
        loc_max = output_json['config']['edgesLocalityDistribution']['max']
        results['locality-min'].append(loc_min)
        results['locality-max'].append(loc_max)
        results['locality'].append(f'[{loc_min}\\comma {loc_max}]')
        results['ps-mu-steps'].append(f"{output_json['perfectSamplingOutput']['avgSteps']:.2f}")
        results['ps-sigma-steps'].append(f"{output_json['perfectSamplingOutput']['sigma']:.2f}")
        results['ps-distance'].append(f"{output_json['perfectSamplingOutput']['distance']:.3E}")
        results['d0-steps'].append(output_json['dumbSamplingOutputs'][0]['steps'])
        results['d0-distance'].append(f"{output_json['dumbSamplingOutputs'][0]['distance']:.3E}")
        results['d1-steps'].append(output_json['dumbSamplingOutputs'][1]['steps'])
        results['d1-distance'].append(f"{output_json['dumbSamplingOutputs'][1]['distance']:.3E}")
        results['d2-steps'].append(output_json['dumbSamplingOutputs'][2]['steps'])
        results['d2-distance'].append(f"{output_json['dumbSamplingOutputs'][2]['distance']:.3E}")
    df = pd.DataFrame.from_dict(results)
    df = df.sort_values(by=['N', 'edges-number', 'locality-max'])
    df.to_csv('../files/perfect-dumb-analysis.csv', index=False)
    pass

if __name__ == '__main__':
    main()
