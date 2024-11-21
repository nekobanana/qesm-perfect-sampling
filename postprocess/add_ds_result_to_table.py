import os

import pandas as pd

from perfect_dumb_analysis import generate_RQ2_table

def main(old_table_path, new_java_results_dir, new_table_path):
    table = pd.read_csv(old_table_path)
    generate_RQ2_table(new_java_results_dir, f'{new_table_path}_tmp.csv')
    new = pd.read_csv(f'{new_table_path}_tmp.csv')
    table['ds-fc0.95-steps'] = new['d0.0-steps']
    table['ds-fc0.95-distance'] = new['d0.0-distance']
    os.remove(f'{new_table_path}_tmp.csv')
    table.to_csv(f'{new_table_path}.csv', index=False)
    pass


if __name__ == '__main__':
    main('../results_history/same_DTMC_2/20241115-090238-single-random/tables/table_perfect_dumb.csv',
         '../results_history/same_DTMC_2/20241120-181516-dumb-coupling-time-1R/files/output',
         '../results_history/same_DTMC_2/20241120-181516-dumb-coupling-time-1R/comparison_ds')
