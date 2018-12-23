package io.github.robertograham.fortnite2.implementation;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.bind.adapter.JsonbAdapter;
import java.util.List;
import java.util.Objects;

final class Cohort {

    private final List<String> cohortAccounts;

    private Cohort(List<String> cohortAccounts) {
        this.cohortAccounts = cohortAccounts;
    }

    List<String> cohortAccounts() {
        return cohortAccounts;
    }

    @Override
    public String toString() {
        return "Cohort{" +
                "cohortAccounts=" + cohortAccounts +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (!(object instanceof Cohort))
            return false;
        Cohort cohort = (Cohort) object;
        return cohortAccounts.equals(cohort.cohortAccounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cohortAccounts);
    }

    enum Adapter implements JsonbAdapter<Cohort, JsonObject> {

        INSTANCE;

        @Override
        public JsonObject adaptToJson(Cohort cohort) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Cohort adaptFromJson(JsonObject jsonObject) {
            return new Cohort(
                    jsonObject.getJsonArray("cohortAccounts")
                            .getValuesAs(JsonString::getString)
            );
        }
    }
}