import json
def main():
    # Definizione della tabella dei valori
    table = [
        [4, 2, -2, 2],
        [16, 2, -2, 2],
        [16, 2, -4, 4],
        [16, 2, -8, 8],
        [16, 4, -2, 2],
        [16, 4, -4, 4],
        [16, 4, -8, 8],
        [16, 8, -2, 2],
        [16, 8, -4, 4],
        [16, 8, -8, 8],
        [64, 3, -3, 3],
        [64, 3, -6, 6],
        [64, 3, -12, 12],
        [64, 6, -3, 3],
        [64, 6, -6, 6],
        [64, 6, -12, 12],
        [64, 12, -3, 3],
        [64, 12, -6, 6],
        [64, 12, -12, 12],
        [256, 4, -4, 4],
        [256, 4, -8, 8],
        [256, 4, -16, 16],
        [256, 8, -4, 4],
        [256, 8, -8, 8],
        [256, 8, -16, 16],
        [256, 16, -4, 4],
        [256, 16, -8, 8],
        [256, 16, -16, 16]
    ]

    # Modello di configurazione
    config_template = {
        "seed": None,
        "description": "Dumb sampling based on 0.95 quantile of forward coupling",
        "dtmcGeneratorConfig": {
            "n": None,  # Questo sarà sostituito
            "connectSCCs": False,
            "edgesNumberDistribution": {
                "n": None,  # Questo sarà sostituito
                "distributionType": "org.qesm.app.model.generator.distribution.SingleValueDistribution"
            },
            "edgesLocalityDistribution": {
                "min": None,  # Questo sarà sostituito
                "max": None,  # Questo sarà sostituito
                "distributionType": "org.qesm.app.model.generator.distribution.UniformDistribution"
            },
            "selfLoopValue": None,
        },
        "perfectSamplingConfig": {
            "enabled" : False,
            "statisticalTestConfig": {
                "confidence": 0.95,
                "error": 0.01,
                "testClass": "org.qesm.app.model.test.ZTest"
            },
            "randomHelperClass": "org.qesm.app.model.sampling.sampler.random.NRandomHelper",
            "pythonHistogramImage": True,
            "pythonLastSequenceImage": False,
        },
        "dumbSamplingConfig": {
            "enabled": True,
            "sigmas": [-2.0, -1.0, 0.0, 1.0, 2.0],
            "usePerfectSamplingOutput": False,
            "customMean": None,
            "customStdDev": None,
            "customSamplesNumber": None
        },
        "transientAnalysisConfig": {
            "enabled": False,
            "maxDistanceToSteadyState": 0.0001
        },
        "forwardSamplingConfig": {
            "enabled": False,
            "randomHelperClass": "org.qesm.app.model.sampling.sampler.random.NRandomHelper"
        },
        "forwardCouplingConfig": {
            "enabled": False
            # "usePerfectSamplingSampleSize": False,
            # "SampleSize": None
        }
    }

    # Generazione dei file
    for index, (N, n_edges, outgoing_edges_min, outgoing_edges_max) in enumerate(table):
        # Creazione della configurazione specifica
        config = config_template.copy()
        config["dtmcGeneratorConfig"]["edgesNumberDistribution"]["n"] = n_edges
        config["dtmcGeneratorConfig"]["edgesLocalityDistribution"]["min"] = outgoing_edges_min
        config["dtmcGeneratorConfig"]["edgesLocalityDistribution"]["max"] = outgoing_edges_max
        config["dtmcGeneratorConfig"]["n"] = N
        # config["forwardCouplingConfig"]["sampleSize"] = N

        # Nome del file
        filename = f"{index}.json"

        # Scrittura del file JSON
        with open(f'../files/input/{filename}', 'w') as file:
            json.dump(config, file, indent=4)

    print("Done")


if __name__ == "__main__":
    main()