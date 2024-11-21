import json
import os
from collections import defaultdict

import pandas as pd

def generate_RQ2_table(java_results_dir, table_path):
    results = defaultdict(lambda: [])
    for output_file in os.listdir(java_results_dir):
        output_file_path = os.path.join(java_results_dir, output_file)
        output_json = json.load(open(output_file_path))
        results['file-name'].append(output_json['fileName'])
        results['N'].append(output_json['config']['dtmcGeneratorConfig']['n'])
        results['edges-number'].append(output_json['config']['dtmcGeneratorConfig']['edgesNumberDistribution']['n'])
        loc_min = output_json['config']['dtmcGeneratorConfig']['edgesLocalityDistribution']['min']
        loc_max = output_json['config']['dtmcGeneratorConfig']['edgesLocalityDistribution']['max']
        results['locality-min'].append(loc_min)
        results['locality-max'].append(loc_max)
        if output_json['perfectSamplingOutput'] is not None:
            results['locality'].append(f'[{loc_min}\\comma {loc_max}]')
            results['ps-mu-steps'].append(f"{output_json['perfectSamplingOutput']['avgSteps']:.2f}")
            results['ps-sigma-steps'].append(f"{output_json['perfectSamplingOutput']['sigma']:.2f}")
            results['ps-distance'].append(f"{output_json['perfectSamplingOutput']['distance']:.3E}")
        for ds_output in output_json['dumbSamplingOutputs']:
            sigma = ds_output['sigmas']
            results[f'd{sigma}-steps'].append(ds_output['steps'])
            results[f'd{sigma}-distance'].append(f"{ds_output['distance']:.3E}")
    df = pd.DataFrame.from_dict(results)
    df = df.sort_values(by=['N', 'edges-number', 'locality-max'])
    df.to_csv(table_path, index=False)
    pass

if __name__ == '__main__':
    generate_RQ2_table(java_results_dir='../files/output', table_path='../files/perfect-dumb-analysis.csv')
