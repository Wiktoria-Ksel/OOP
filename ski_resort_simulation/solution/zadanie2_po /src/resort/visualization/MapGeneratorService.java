package resort.visualization;

import kadra.GeneratorMapek;
import kadra.pliki.WyjatekSystemuPlikow;
import kadra.styl.StylKrawedzi;
import kadra.styl.StylLinii;
import kadra.styl.StylWezla;
import kadra.styl.GruboscKonturu;
import resort.model.ResortData;
import resort.model.Node;
import resort.model.Route;
import resort.model.Lift;
import resort.model.Athlete;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Only generated maps are kept in Polish to keep the clarity of the generated graphs.
public class MapGeneratorService {
    private final ResortData data;
    private final String directoryPath;

    public MapGeneratorService(ResortData data, String directoryPath) {
        this.data = data;
        this.directoryPath = directoryPath;
    }

    public void generateAllMaps() throws WyjatekSystemuPlikow {
        if (directoryPath == null) {
            throw new IllegalArgumentException("The directory path cannot be null.");
        }
        GeneratorMapek generator = new GeneratorMapek(directoryPath);

        generateParametersMap(generator);

        generateStatsMap(generator);

        generateHistoryMaps(generator);
    }

    private void addNodes(GeneratorMapek generator) {
        for (Node node : data.getNodes()) {
            StylWezla style = node.isConnected()
                    ? new StylWezla(GruboscKonturu.POGRUBIONY)
                    : new StylWezla(GruboscKonturu.ZWYKLY);
            generator.dodajWezel(node.getId(), node.getX(), node.getY(), style);
        }
    }

    private void generateParametersMap(GeneratorMapek generator) throws WyjatekSystemuPlikow {
        generator.zeruj();
        addNodes(generator);

        for (Route route : data.getRoutes()) {
            StylKrawedzi styl = new StylKrawedzi(StylLinii.CIAGLA);
            List<String> lines = new ArrayList<>();
            lines.add(String.format("t%d: poziom: %d, czas: %ds",
                    route.getId(), route.getDifficulty(), route.getTravelTime()));
            lines.add(String.format(Locale.US, "odporność: %.2f, %.5f",
                    route.getBaseAttractiveness(), route.getResistance()));
            generator.dodajKrawedz(route.getFrom().getId(), route.getTo().getId(), styl, lines);
        }

        for (Lift lift : data.getLifts()) {
            StylKrawedzi styl = new StylKrawedzi(StylLinii.PRZERYWANA);
            List<String> lines = new ArrayList<>();
            lines.add(String.format("w%d: %d os. co %ds",
                    lift.getId(), lift.getCapacity(), lift.getInterval()));
            lines.add(String.format("czas: %ds", lift.getTravelTime()));
            generator.dodajKrawedz(lift.getFrom().getId(), lift.getTo().getId(), styl, lines);
        }

        generator.tworzMapke("parametry.tex");
    }

    private void generateStatsMap(GeneratorMapek generator) throws WyjatekSystemuPlikow {
        generator.zeruj();
        addNodes(generator);

        for (Route route : data.getRoutes()) {
            StylKrawedzi styl = new StylKrawedzi(StylLinii.CIAGLA);
            List<String> lines = new ArrayList<>();
            lines.add(String.format(Locale.US, "t%d: śnieg: %.2f",
                    route.getId(), route.getCurrentSurfaceAttractiveness()));
            lines.add(String.format("zjazdy: %d", route.getRideCount()));
            generator.dodajKrawedz(route.getFrom().getId(), route.getTo().getId(), styl, lines);
        }

        for (Lift lift : data.getLifts()) {
            StylKrawedzi styl = new StylKrawedzi(StylLinii.PRZERYWANA);
            List<String> lines = new ArrayList<>();

            int avgQ = (int) Math.round(lift.getAverageQueueLength());
            lines.add(String.format("w%d: kol: %d(śr), %d(maks)",
                    lift.getId(), avgQ, lift.getMaxQueueLength()));
            lines.add(String.format("wjazdy: %d / %d (%d%%)",
                    lift.getTotalTransported(), lift.getMaxPossiblePassengers(),
                    (int) Math.round(lift.getOccupiedPercentage())));
            generator.dodajKrawedz(lift.getFrom().getId(), lift.getTo().getId(), styl, lines);
        }

        generator.tworzMapke("statystyki.tex");
    }

    private void generateHistoryMaps(GeneratorMapek generator) throws WyjatekSystemuPlikow {
        for (Athlete athlete : data.getAthletes()) {
            if (athlete.isTracked()) {
                generator.zeruj();
                addNodes(generator);

                for (Route route : data.getRoutes()) {
                    List<Integer> seqs = athlete.getHistoryForConnection(route);
                    StylKrawedzi styl = new StylKrawedzi(StylLinii.CIAGLA);
                    String text = formatHistoryText("t", route.getId(), seqs);
                    generator.dodajKrawedz(route.getFrom().getId(), route.getTo().getId(), styl, text);
                }

                for (Lift lift : data.getLifts()) {
                    List<Integer> seqs = athlete.getHistoryForConnection(lift);
                    StylKrawedzi styl = new StylKrawedzi(StylLinii.PRZERYWANA);
                    String text = formatHistoryText("w", lift.getId(), seqs);
                    generator.dodajKrawedz(lift.getFrom().getId(), lift.getTo().getId(), styl, text);
                }

                generator.tworzMapke(String.format("historia_sportowca_%d.tex", athlete.getId()));
            }
        }
    }

    private String formatHistoryText(String prefix, int id, List<Integer> seqs) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s%d(%d):", prefix, id, seqs.size()));
        if (!seqs.isEmpty()) {
            sb.append(" ");
            for (int i = 0; i < seqs.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(seqs.get(i));
            }
        }
        return sb.toString();
    }
}