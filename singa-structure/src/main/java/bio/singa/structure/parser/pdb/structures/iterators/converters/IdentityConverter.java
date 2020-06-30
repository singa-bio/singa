package bio.singa.structure.parser.pdb.structures.iterators.converters;

/**
 * @author cl
 */
public class IdentityConverter<Type> implements ContentConverter<Type, Type> {

    public static <StaticType> IdentityConverter<StaticType> get(Class<StaticType> typeClass) {
        return new IdentityConverter<>();
    }

    private IdentityConverter() {

    }

    @Override
    public Type convert(Type content) {
        return content;
    }

}
