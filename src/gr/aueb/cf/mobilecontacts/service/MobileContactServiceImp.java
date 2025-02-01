package gr.aueb.cf.mobilecontacts.service;

import gr.aueb.cf.mobilecontacts.dao.IMobileContactDAO;
import gr.aueb.cf.mobilecontacts.dto.MobileContactInsertDTO;
import gr.aueb.cf.mobilecontacts.dto.MobileContactUpdateDTO;
import gr.aueb.cf.mobilecontacts.exceptions.ContactNotFoundException;
import gr.aueb.cf.mobilecontacts.exceptions.PhoneNumberAlreadyExistsException;
import gr.aueb.cf.mobilecontacts.model.MobileContact;

import java.util.List;

public class MobileContactServiceImp implements IMobileContactService{

    private final IMobileContactDAO dao;

    public MobileContactServiceImp(IMobileContactDAO dao) {
        this.dao = dao;
    }

    @Override
    public MobileContact insertMobileContact(MobileContactInsertDTO dto)
            throws PhoneNumberAlreadyExistsException {
        MobileContact mobileContact;

        try {
            if (dao.phoneNumberExist(dto.getPhoneNumber())) {
                throw new PhoneNumberAlreadyExistsException("Contact with phone number " + dto.getPhoneNumber() + " already exist.");
            }
            mobileContact = mapInsertDTOToContact(dto);

            System.err.printf("MobileContactServiceImp Logger: %s was insert", mobileContact);
            return dao.insert(mobileContact);
        } catch (PhoneNumberAlreadyExistsException e) {
            System.err.printf("MobileContactServiceImp Logger: contact with phone number: %s already exist", dto.getPhoneNumber());
            throw e;
        }
    }

    @Override
    public MobileContact updateMobileContact(MobileContactUpdateDTO dto)
            throws PhoneNumberAlreadyExistsException, ContactNotFoundException {
        MobileContact mobileContact;
        MobileContact newContact;
        try {
            if (!dao.userIdExist(dto.getId())) {
                throw new ContactNotFoundException("Contact with id: " + dto.getId() + " not found for update");
            }

            mobileContact = dao.getById(dto.getId());
            boolean isPhoneNumberOurOwn = mobileContact.getPhoneNumber().equals(dto.getPhoneNumber());
            boolean isPhoneNumberExists = dao.phoneNumberExist(dto.getPhoneNumber());

            if (isPhoneNumberExists && !isPhoneNumberOurOwn) {
                throw new PhoneNumberAlreadyExistsException("Contact with phone number: " + dto.getPhoneNumber() + " already exists and cannot be updated");
            }

            newContact = mapUpdateDTOToContact(dto);
            System.err.printf("MobileContactServiceImp Logger: %s was updated with new info: %s ", mobileContact, newContact);
            return dao.update(dto.getId(), newContact);
        } catch (ContactNotFoundException | PhoneNumberAlreadyExistsException e) {
            System.err.printf(e.getMessage());
            throw e;
        }

    }

    @Override
    public void deleteContactById(Long id) throws ContactNotFoundException {

    }

    @Override
    public MobileContact getContactById(Long id) throws ContactNotFoundException {
        return null;
    }

    @Override
    public List<MobileContact> getAllContacts() {
        return List.of();
    }

    @Override
    public MobileContact getContactByPhoneNumber(String phoneNumber) throws ContactNotFoundException {
        return null;
    }

    @Override
    public void deleteContactByPhoneNumber(String phoneNumber) throws ContactNotFoundException {

    }

    private MobileContact mapInsertDTOToContact(MobileContactInsertDTO dto) {
        return new MobileContact(null, dto.getFirstname(), dto.getLastname(), dto.getPhoneNumber());
    }

    private MobileContact mapUpdateDTOToContact(MobileContactUpdateDTO dto) {
        return new MobileContact(dto.getId(), dto.getFirstname(), dto.getLastname(), dto.getPhoneNumber());
    }
}
