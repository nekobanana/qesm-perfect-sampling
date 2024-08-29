import json
import math
import os

import numpy as np
import scipy.stats as stats
import matplotlib.pyplot as plt
import pkg_resources


def histogram(results_json):
    # Load the data
    with open(results_json) as f:
        data = json.load(f)
    parent_dir = os.path.dirname(os.path.join(results_json))
    # Extract the 'steps' values
    steps = [entry['steps'] for entry in data]

    # Fit various distributions and perform goodness-of-fit tests
    distributions = ['norm', 'expon', 'gamma', 'lognorm', 'beta', 'weibull_min', 'erlang']
    results = []

    for dist_name in distributions:
        dist = getattr(stats, dist_name)
        if dist_name == 'erlang':
            param = fit_erlang(steps, 10)
        else:
            param = dist.fit(steps)

        # Kolmogorov-Smirnov test
        D, p_value = stats.kstest(steps, dist_name, args=param)
        results.append({
            'name': dist_name,
            'D': D,
            'p-value': p_value,
            'parameters': param
        })

    # Find the best fit based on the highest p-value
    best_fit = max(results, key=lambda x: x['p-value'])
    # best_dist_name, best_D, best_p_value, best_param = best_fit

    for b in [max(steps) - min(steps) + 1, 20]:
        plt.figure(figsize=(10, 6))
        plt.hist(steps, bins=b, density=False, alpha=0.6, color='g', label='Data')
        plt.savefig(f'{parent_dir}/hist{"_"+str(b) if b != max(steps) else ""}.png')

    # Plotting the histogram of the data and the best fit distribution
    for b in [max(steps) - min(steps) + 1]:
    # for b in [max(steps) - min(steps) + 1, 20]:
        plt.figure(figsize=(10, 6))
        x = np.linspace(min(steps), max(steps), 1000)
        observed_freq, bin_edges = np.histogram(np.array(steps), bins=b)
        plt.bar(bin_edges[:-1], observed_freq / len(steps), width=np.diff(bin_edges), align="edge", alpha=0.3, color='grey')
        colormap = plt.get_cmap('Set2').colors
        color = iter(colormap)
        for dist in results:
            pdf = getattr(stats, dist['name']).pdf(x, *dist['parameters'])
            plt.plot(x, pdf, '-', c=next(color), label=f'{dist["name"]}' + (' (best fit)' if dist["name"] == best_fit["name"] else ''))
        plt.legend()
        plt.ylim([0, 1.05 * max(observed_freq) / len(steps)])
        plt.xlabel('Steps')
        plt.ylabel('Density')
        plt.title('Histogram of Steps with Best Fit Distribution')
        plt.savefig(f'{parent_dir}/hist_fit{"_"+str(b) if b != max(steps) else ""}.png')

    print(f'Best Fit Distribution: {best_fit["name"]}')
    for dist in results:
        print(f'{dist["name"]}:\t D={dist["D"]},\t p={dist["p-value"]}')

    r = {
        'best_fit': best_fit["name"],
        'results': results
    }
    r_path = os.path.join(parent_dir, 'best_fit.json')
    with open(r_path, 'w') as fp:
        json.dump(r, fp, indent=4)


def fit_erlang(data, k_max=10):
    """
    By ChatGPT

    Fitta una distribuzione Erlang ai dati forniti.

    Parameters:
    data (array-like): I dati su cui effettuare il fitting.
    k_max (int): Il valore massimo da considerare per il parametro k. Default è 10.

    Returns:
    best_k (int): Il valore ottimale del parametro k.
    loc (float): Il parametro loc della distribuzione fittata.
    scale (float): Il parametro scale della distribuzione fittata.
    fitted_distribution (scipy.stats.rv_continuous): L'oggetto rappresentante la distribuzione Erlang fittata.
    """

    # Funzione per calcolare la log-verosimiglianza negativa di una distribuzione Gamma/Erlang
    def negative_log_likelihood(k, data):
        # Vincola k ad essere intero
        k = int(np.round(k))
        loc, scale = 0, np.mean(data) / k  # Stima iniziale per loc e scale
        nll = -np.sum(stats.gamma.logpdf(data, a=k, loc=loc, scale=scale))
        return nll

    # Testa diversi valori di k
    k_values = np.arange(1, k_max + 1)
    nll_values = [negative_log_likelihood(k, data) for k in k_values]

    # Trova il valore di k che minimizza la log-verosimiglianza negativa
    best_k = k_values[np.argmin(nll_values)]

    # Calcola i parametri finali della distribuzione Erlang fittata
    loc, scale = 0, np.mean(data) / best_k
    # fitted_distribution = stats.gamma(a=best_k, loc=loc, scale=scale)

    return int(best_k), loc, scale


def gaussian(results_json):
    # Load the data
    with open(results_json) as f:
        data = json.load(f)
    parent_dir = os.path.dirname(os.path.join(results_json))
    steps = [entry['steps'] for entry in data]
    log_steps = [math.log(s) for s in steps]
    norm = getattr(stats, 'norm')
    param = norm.fit(log_steps)
    D, p_value = stats.kstest(log_steps, 'norm', args=param)

    # b = int(max(steps) - min(steps) + 1)
    plt.figure(figsize=(10, 6))
    plt.hist(log_steps, density=True, bins=50, alpha=0.6, label='Data')
    x = np.linspace(min(log_steps), max(log_steps), 1000)
    pdf = norm.pdf(x, *param)
    plt.plot(x, pdf, '-', linewidth=3, label='normal')
    plt.show()
    print(f'normal:\t D={D},\t p={p_value}')

