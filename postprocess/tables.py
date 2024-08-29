import getopt
import json
import os
import sys

from generate_table import generate_RQ1_table
from perfect_dumb_analysis import generate_RQ2_table

def main(argument_list):
    options = "t:"
    long_options = ["tables"]
    try:
        arguments, values = getopt.getopt(argument_list, options, long_options)
        for current_argument, current_value in arguments:
            if current_argument in ("-t", "--tables"):
                print('Generating tables...')
                paths = current_value.split(';')
                assert (len(paths) == 4)
                generate_RQ1_table(java_results_dir=paths[0], table1_path=paths[1], table2_path=paths[2])
                generate_RQ2_table(java_results_dir=paths[0], table_path=paths[1])
    except getopt.error as err:
        print(str(err))


if __name__ == '__main__':

    main(sys.argv[1:])
    