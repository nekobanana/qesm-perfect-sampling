import json
import os

import pandas as pd


def main():
    d = []
    directory = '../files/output'
    for i, file in enumerate(os.listdir(os.fsencode(directory))):
        filename = os.fsdecode(file)
        with open(os.path.join(directory, filename)) as f:
            data = json.load(f)
            name = data['fileName']
            n = data['config']['n']
            e_number = data['config']['edgesNumberDistribution']['n']
            e_locality_min = data['config']['edgesLocalityDistribution']['min']
            e_locality_max = data['config']['edgesLocalityDistribution']['max']
            mean = data['perfectSamplingOutput']['avgSteps']
            stddev = data['perfectSamplingOutput']['sigma']
            samples_n = data['perfectSamplingOutput']['statisticalTest']['samplesSize']
            error = data['perfectSamplingOutput']['statisticalTest']['currentError']
            d.append({'N': n, 'n_edges': e_number, 'outgoing_edges_min': e_locality_min,
                      'outgoing_edges_max': e_locality_max, 'mean': mean, 'std_dev': stddev,
                      'samples_n': samples_n, 'error': error
                      # 'file_name': name,
                      })
    df = pd.DataFrame(d)
    df = df.sort_values(by=['N', 'n_edges', 'outgoing_edges_max'])
    df.to_csv('../files/table.csv', header=True, index=False)

if __name__ == "__main__":
    main()
