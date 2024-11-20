import pandas as pd

# from perfect_dumb_analysis import generate_RQ2_table

def main(old_table_path, new_java_results_dir, new_table_path):
    table = pd.read_csv(old_table_path)
    # generate_RQ2_table(new_java_results_dir, f'{new_table_path}_tmp')
    new = pd.read_csv(f'{new_table_path}_tmp')
    # table['ds-fc-steps'] = new['ds-fc-steps']
    # table['ds-fc-distance']


if __name__ == '__main__':
    main('../results_history/same_DTMC_2/20241115-090238-single-random/tables/table_perfect_dumb.csv',
         '../results_history/same_DTMC_2/20241120-181516-dumb-coupling-time-1R/files/output',
         '../results_history/same_DTMC_2/20241120-181516-dumb-coupling-time-1R/comparison_ds')
