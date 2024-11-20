import json
import os

from streamerate import stream


def main():
    for filename in os.listdir(f'../files/output'):
        ps_output = None
        with open(f'jensen_shannon/same_DTMC_2_coupling/distributions/{filename}', 'r') as f:
            distr_data = json.load(f)
            forward_coupling_data = stream(distr_data).filter(lambda x: x['name'] == 'forward coupling').toList()[0]
        with open(f'../files/input/{filename}', 'r+') as f:
            input_config = json.load(f)
            input_config['dumbSamplingConfig']['usePerfectSamplingOutput'] = False
            input_config['dumbSamplingConfig']['customMean'] = forward_coupling_data['quantiles']['0.95']
            input_config['dumbSamplingConfig']['customStdDev'] = 0
            input_config['dumbSamplingConfig']['customSamplesNumber'] = input_config['previousPerfectSamplingOutput']['statisticalTest']['samplesSize']
            input_config['dumbSamplingConfig']['sigmas'] = [0]
            f.seek(0)  # <--- should reset file position to the beginning.
            json.dump(input_config, f, indent=4)
            f.truncate()  # remove remaining part

    print("Done")


if __name__ == "__main__":
    main()