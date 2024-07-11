import json
def main():
    # Definizione della tabella dei valori
    table = [
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
        "seed": None,
        "statisticalTestConfig": {
            "confidence": 0.95,
            "error": 0.01,
            "testClass": "org.qesm.app.model.test.ZTest"
          },
        "connectSCCs": False,
        "description": "",
        "pythonHistogramImage": True,
        "pythonLastSequenceImage": False,
        "n": None  # Questo sarà sostituito
    }

    # Generazione dei file
    for index, (N, n_edges, outgoing_edges_min, outgoing_edges_max) in enumerate(table):
        # Creazione della configurazione specifica
        config = config_template.copy()
        config["edgesNumberDistribution"]["n"] = n_edges
        config["edgesLocalityDistribution"]["min"] = outgoing_edges_min
        config["edgesLocalityDistribution"]["max"] = outgoing_edges_max
        config["n"] = N

        # Nome del file
        filename = f"input{index}.json"

        # Scrittura del file JSON
        with open(f'../files/input/{filename}', 'w') as file:
            json.dump(config, file, indent=4)

    print("Generazione dei file completata.")

if __name__ == "__main__":
    main()