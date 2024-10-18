import json
import os

import pandas as pd

def generate_RQ1_table(java_results_dir, table1_path, table2_path):
    d = []
    for i, file in enumerate(os.listdir(os.fsencode(java_results_dir))):
        filename = os.fsdecode(file)
        with open(os.path.join(java_results_dir, filename)) as f:
            data = json.load(f)
            name = data['fileName']
            n = data['config']['dtmcGeneratorConfig']['n']
            e_number = data['config']['dtmcGeneratorConfig']['edgesNumberDistribution']['n']
            loc_min = data['config']['dtmcGeneratorConfig']['edgesLocalityDistribution']['min']
            loc_max = data['config']['dtmcGeneratorConfig']['edgesLocalityDistribution']['max']
            e_locality = f'[{loc_min}\\comma {loc_max}]'
            e_locality_max = loc_max
            mean = f"{data['perfectSamplingOutput']['avgSteps']:.2f}"
            stddev = f"{data['perfectSamplingOutput']['sigma']:.2f}"
            samples_n = data['perfectSamplingOutput']['statisticalTest']['samplesSize']
            error = f"{data['perfectSamplingOutput']['statisticalTest']['currentError']}"
            # color = 'GreenYellow'
            d.append({'N': n, 'n_edges': e_number, 'outgoing_edges': e_locality,
                      'outgoing_edges_max': e_locality_max, 'mean': mean, 'std_dev': stddev,
                      'samples_n': samples_n,
                      'error': error,
                      'file_name': name,
                      # 'color': color,
                      })
    df = pd.DataFrame(d)
    df = df.sort_values(by=['N', 'n_edges', 'outgoing_edges_max'])
    df.to_csv(table1_path, header=True, index=False)
    df = df.sort_values(by=['N', 'outgoing_edges_max', 'n_edges'])
    df.to_csv(table2_path, header=True, index=False)

def analyze_std_dev():
    df = pd.read_csv('../files/table.csv')
    std_dev_avg_ratios = []
    for r in df.iterrows():
        std_dev_avg_ratios.append(r[1]['std_dev'] / r[1]['mean'])
    print(std_dev_avg_ratios)


if __name__ == "__main__":
    generate_RQ1_table(java_results_dir='../files/output', table1_path='../files/table1_1.csv', table2_path='../files/table1_2.csv')
    # analyze_std_dev()