package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE_AND_SLOT_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_UID;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.DateSlot;
import seedu.address.model.person.Nurse;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;
import seedu.address.model.person.Uid;

/**
 * Unmarks a patient's dateslot using their unique id and dateslot index when
 * fail to visit.
 */
public class UnmarkCommand extends Command {

    public static final String COMMAND_WORD = "unmark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unmarks the patient's dateslot identified by the unique id number in the displayed person list "
            + "and dateslot index as fail to visit.\n"
            + "Parameters: UID (must be a positive integer) " + PREFIX_DATE_AND_SLOT_INDEX + "Date_Slot_Index"
            + "Example: " + COMMAND_WORD + " " + PREFIX_UID + " 1" + PREFIX_DATE_AND_SLOT_INDEX + "2";

    public static final String MESSAGE_UNMARK_PATIENT_SUCCESS = "Unmarked Patient as fail to visit: %1$s";
    public static final String MESSAGE_INVALID_NURSE_UID = "This uid gives a nurse." + " Please recheck the uid. "
            + "Unmark is only for patient.";
    public static final String MESSAGE_OUT_OF_BOUND_DATE_AND_SLOT_INDEX = "The given date slot index is out of bounds."
            + "Please recheck the index.";
    public static final String MESSAGE_INVALID_DATE_AND_SLOT_INDEX = "The visit dates has not reached."
            + "Cannot unmark it as fail to visit.";

    private final Uid uid;
    private final Index dateSlotIndex;

    /**
     * Initialises the MarkCommand with a valid and non-null {@code Uid} of the
     * target patient.
     */
    public UnmarkCommand(Uid uid, Index dateSlotIndex) {
        requireNonNull(uid);
        requireNonNull(dateSlotIndex);
        this.uid = uid;
        this.dateSlotIndex = dateSlotIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        Optional<Person> targetPerson = lastShownList.stream().filter(p -> p.getUid().equals(uid)).findFirst();

        if (targetPerson.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_UID);
        }

        Person personToUnmark = targetPerson.get();

        if (personToUnmark instanceof Nurse) {
            throw new CommandException(MESSAGE_INVALID_NURSE_UID);
        }

        unmarkAction(personToUnmark, model);

        return new CommandResult(String.format(MESSAGE_UNMARK_PATIENT_SUCCESS, personToUnmark));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UnmarkCommand// instanceof handles nulls
                        && this.uid.equals(((UnmarkCommand) other).uid) // state check
                        && this.dateSlotIndex.equals(((UnmarkCommand) other).dateSlotIndex));
    }

    private void unmarkAction(Person personToUnmark, Model model) throws CommandException {
        List<DateSlot> dateSlotList = ((Patient) personToUnmark).getDatesSlots();
        List<DateSlot> updatedDateSlotList = new ArrayList<>(dateSlotList);

        if (dateSlotIndex.getZeroBased() >= dateSlotList.size()) {
            throw new CommandException(MESSAGE_OUT_OF_BOUND_DATE_AND_SLOT_INDEX);
        }

        DateSlot dateToBeUnmark = updatedDateSlotList.get(dateSlotIndex.getZeroBased());

        if (!dateToBeUnmark.getHasVisited()) {
            throw new CommandException(MESSAGE_INVALID_DATE_AND_SLOT_INDEX);
        }

        dateToBeUnmark.markFail();
        editPatient(model, personToUnmark, updatedDateSlotList);
    }

    private void editPatient(Model model, Person patient, List<DateSlot> dateSlotList) {
        Uid uid = patient.getUid();
        List<Person> lastShownList = model.getFilteredPersonList();
        Optional<Person> personToEdit = lastShownList.stream().filter(p -> p.getUid().equals(uid)).findFirst();
        Person confirmedPersonToEdit = personToEdit.get();
        Person newPerson = new Patient(confirmedPersonToEdit.getUid(), confirmedPersonToEdit.getName(),
                confirmedPersonToEdit.getGender(), confirmedPersonToEdit.getPhone(), confirmedPersonToEdit.getEmail(),
                confirmedPersonToEdit.getAddress(), confirmedPersonToEdit.getTags(), dateSlotList);
        model.setPerson(confirmedPersonToEdit, newPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }
}
