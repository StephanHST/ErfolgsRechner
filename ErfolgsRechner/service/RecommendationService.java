
package Java.ErfolgsRechner.service;

public class RecommendationService {

    public String buildRecommendationForLever(String leverName) {
        switch (leverName) {
            case "Energie":
                return "Der größte Hebel liegt aktuell in deiner Energie: weniger Druck, klare Pausen, Bewegung, Schlaf und nur die wichtigsten Aufgaben.";
            case "Emotionale Stabilität":
                return "Der größte Hebel liegt aktuell in deiner emotionalen Stabilität: offene Schleifen schließen, Erwartungen senken, Reize reduzieren und für mehr Ruhe im System sorgen.";
            case "Ressourcen":
                return "Der größte Hebel liegt aktuell bei deinen Ressourcen: priorisieren, delegieren, vereinfachen oder verschieben.";
            case "Wissenstand":
                return "Der größte Hebel liegt aktuell im Wissen: kurz recherchieren, nachfragen oder eine Wissenslücke gezielt schließen.";
            case "Zielklarheit":
                return "Der größte Hebel liegt aktuell in deiner Zielklarheit: den nächsten Schritt sauber definieren und die Richtung schärfen.";
            case "Konzentration":
                return "Der größte Hebel liegt aktuell in deiner Konzentration: Ablenkung reduzieren und Fokus bewusst schützen.";
            case "Zeit":
                return "Der größte Hebel liegt aktuell in deiner Zeit: bewusst ein größeres Zeitfenster freischaufeln und Prioritäten schärfen.";
            default:
                return "Bitte zuerst eine Messung durchführen.";
        }
    }

    public String buildSecondLeverHint(String leverName) {
        if (leverName == null || leverName.trim().isEmpty()) {
            return "";
        }

        switch (leverName) {
            case "Energie":
                return " Versuche bitte zusätzlich deine Energie etwas bewusster zu schützen.";
            case "Emotionale Stabilität":
                return " Am Rande macht es auch Sinn, etwas mehr emotionale Ruhe in dein System zu bringen.";
            case "Ressourcen":
                return " Um mehr Stabilität zu generieren, ordne deine Ressourcen etwas klüger bzw. suche dir passende Hilfe.";
            case "Wissenstand":
                return " Damit du weißt was du tust, solltest du deine kleine Wissenslücke gezielt schließen.";
            case "Zielklarheit":
                return " Damit du deine Energie nicht verpulverst, zieh dein Ziel noch etwas klarer und entscheide den nächsten Schritt sauber.";
            case "Konzentration":
                return " Damit deine Energie nicht verpufft, schütze deinen Fokus etwas konsequenter vor Ablenkung.";
            case "Zeit":
                return " Damit dein Vorhaben wirklich Raum bekommt, schaufle dir zusätzlich ein wenig mehr Zeit frei.";
            default:
                return " Behalte daneben auch den zweitgrößten Hebel im Blick, weil dort zusätzlicher Schwung entstehen kann.";
        }
    }
}
