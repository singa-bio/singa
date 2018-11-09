package bio.singa.features.identifiers.model;

import bio.singa.features.identifiers.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class IdentifierPatternRegistry {

    private static IdentifierPatternRegistry instance = new IdentifierPatternRegistry();

    static {
        addPattern(ChEBIIdentifier.class, ChEBIIdentifier.PATTERN);
        addPattern(ECNumber.class, ECNumber.PATTERN);
        addPattern(ENAAccessionNumber.class, ENAAccessionNumber.PATTERN);
        addPattern(InChIKey.class, InChIKey.PATTERN);
        addPattern(NCBITaxonomyIdentifier.class, NCBITaxonomyIdentifier.PATTERN);
        addPattern(PfamIdentifier.class, PfamIdentifier.PATTERN);
        addPattern(PubChemIdentifier.class, PubChemIdentifier.PATTERN);
        addPattern(SimpleStringIdentifier.class, SimpleStringIdentifier.PATTERN);
        addPattern(UniProtIdentifier.class, UniProtIdentifier.PATTERN);
        addPattern(UniProtEntryName.class, UniProtEntryName.PATTERN);
        addPattern(PDBLigandIdentifier.class, PDBLigandIdentifier.PATTERN);
        addPattern(GoTerm.class, GoTerm.PATTERN);
    }

    private final Map<Class<? extends Identifier>, Pattern> identifierPatternRegistry;

    private IdentifierPatternRegistry() {
        identifierPatternRegistry = new HashMap<>();
    }

    public static IdentifierPatternRegistry getInstance() {
        if (instance == null) {
            synchronized (IdentifierPatternRegistry.class) {
                instance = new IdentifierPatternRegistry();
            }
        }
        return instance;
    }

    public static synchronized <IdentifierType extends Identifier> void addPattern(Class<IdentifierType> identifierClass, Pattern pattern) {
        getInstance().identifierPatternRegistry.put(identifierClass, pattern);
    }

    /**
     * Returns true, if the identifier is valid.
     *
     * @param identifierClass The target identifier class the identifier should be checked against.
     * @param identifier The identifier to check.
     * @param <IdentifierType> The type of the identifier.
     * @return True, if the identifier is valid.
     */
    public static <IdentifierType extends Identifier> boolean check(Class<IdentifierType> identifierClass, Identifier identifier) {
        return check(getInstance().getPattern(identifierClass), identifier);
    }

    /**
     * Searches a valid identifier in a collection of identifiers and returns it.
     *
     * @param identifierClass The target identifier class the identifier should be checked against.
     * @param identifiers A collection of identifiers.
     * @param <IdentifierType> The type of the identifier.
     * @return The first identifier matching the pattern or an empty optional if no identifier could be found.
     */
    public static <IdentifierType extends Identifier> Optional<IdentifierType> find(Class<IdentifierType> identifierClass, Collection<Identifier> identifiers) {
        Pattern pattern = getInstance().getPattern(identifierClass);
        for (Identifier identifier : identifiers) {
            if (check(pattern, identifier)) {
                return Optional.of(identifierClass.cast(identifier));
            }
        }
        return Optional.empty();
    }

    public static Optional<Identifier> instantiate(String identifier) {
        for (Map.Entry<Class<? extends Identifier>, Pattern> classPatternEntry : getInstance().identifierPatternRegistry.entrySet()) {
            if (!classPatternEntry.getKey().equals(SimpleStringIdentifier.class)) {
                if (classPatternEntry.getValue().matcher(identifier).matches()) {
                    try {
                        return Optional.of(classPatternEntry.getKey().getConstructor(String.class).newInstance(identifier));
                    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        e.printStackTrace();
                        return Optional.empty();
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static boolean check(Pattern pattern, Identifier identifier) {
        return pattern.matcher(identifier.toString()).matches();
    }

    private <IdentifierType extends Identifier> Pattern getPattern(Class<IdentifierType> identifierClass) {
        Pattern pattern = getInstance().identifierPatternRegistry.get(identifierClass);
        if (pattern == null) {
            throw new IllegalStateException("The identifier " + identifierClass.getSimpleName() + " has no associated " +
                    "pattern in the pattern registry. Use the addPattern(Class<IdentifierType>, Pattern) method to add" +
                    " a pattern.");
        }
        return pattern;
    }


}
