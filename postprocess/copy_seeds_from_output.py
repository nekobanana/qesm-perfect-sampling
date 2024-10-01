import json
import os


def main():
    for filename in os.listdir(f'../files/output'):
        seed = None
        with open(f'../files/output/{filename}', 'r') as f:
            output_config = json.load(f)
            seed = output_config['config']['seed']
        with open(f'../files/input/{filename}', 'r+') as f:
            input_config = json.load(f)
            input_config['seed'] = seed
            f.seek(0)  # <--- should reset file position to the beginning.
            json.dump(input_config, f, indent=4)
            f.truncate()  # remove remaining part

    print("Done")


if __name__ == "__main__":
    main()