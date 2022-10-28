package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CATEGORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FULLY_ASSIGNED;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FULLY_VISITED;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.ListCommand;
import seedu.address.model.category.Category;
import seedu.address.model.person.Address;
import seedu.address.model.person.Gender;
import seedu.address.model.tag.Tag;

/**
 * Parses user input for the list command.
 */
public class ListCommandParser implements Parser {

    /**
     * Parses user input for the list command.
     *
     * @param args user input, for filtering the list of displayed users
     * @return Filtered list, or list of all users if no filters were specified.
     */
    public ListCommand parse(String args) {
        if (args.length() == 0) {
            return new ListCommand(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty());
        }
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_ADDRESS,
                PREFIX_CATEGORY,
                PREFIX_GENDER,
                PREFIX_TAG,
                PREFIX_FULLY_ASSIGNED,
                PREFIX_FULLY_VISITED);

        Optional<Address> address = argMultimap.getValue(PREFIX_ADDRESS).map(Address::new);
        Optional<Tag> tag = argMultimap.getValue(PREFIX_TAG).map(Tag::new);

        List<Optional<Category>> category = new ArrayList<>();
        argMultimap.getValue(PREFIX_CATEGORY).ifPresentOrElse(
                x -> {
                    if (Category.isValidCategoryName(x.toUpperCase())) {
                        category.add(Optional.of(new Category(x.toUpperCase())));
                    } else {
                        category.add(Optional.empty());
                    }
                }, () -> category.add(Optional.empty()));
        assert (category.size() == 1);

        List<Optional<Gender>> gender = new ArrayList<>();
        argMultimap.getValue(PREFIX_GENDER).ifPresentOrElse(
                x -> {
                    if (Gender.isValidGender(x.toUpperCase())) {
                        gender.add(Optional.of(new Gender(x.toUpperCase())));
                    } else {
                        gender.add(Optional.empty());
                    }
                }, () -> gender.add(Optional.empty()));
        assert (gender.size() == 1);

        List<Optional<Boolean>> fullyAssigned = new ArrayList<>();
        argMultimap.getValue(PREFIX_FULLY_ASSIGNED).ifPresentOrElse(
                x -> fullyAssigned.add(Optional.of(Boolean.valueOf(x))),
                () -> fullyAssigned.add(Optional.empty()));

        List <Optional<Boolean>> fullyVisited = new ArrayList<>();
        argMultimap.getValue(PREFIX_FULLY_VISITED).ifPresentOrElse(
                x -> fullyVisited.add(Optional.of(Boolean.valueOf(x))),
                () -> fullyVisited.add(Optional.empty()));
        return new ListCommand(address, category.get(0), gender.get(0), tag,
                fullyAssigned.get(0), fullyVisited.get(0));
    }

}
