import json
import os


def main():
    for filename in os.listdir(f'../files/output'):
        ps_output = None
        with open(f'../files/output/{filename}', 'r') as f:
            output_config = json.load(f)
            ps_output = output_config['perfectSamplingOutput']
        with open(f'../files/input/{filename}', 'r+') as f:
            input_config = json.load(f)
            input_config['previousPerfectSamplingOutput'] = ps_output
            input_config['previousPerfectSamplingOutput']['statisticalTest']['@class'] = input_config['perfectSamplingConfig']['statisticalTestConfig']['testClass']
            f.seek(0)  # <--- should reset file position to the beginning.
            json.dump(input_config, f, indent=4)
            f.truncate()  # remove remaining part

    print("Done")


if __name__ == "__main__":
    main()