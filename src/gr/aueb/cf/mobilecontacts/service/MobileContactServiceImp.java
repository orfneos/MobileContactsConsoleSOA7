package gr.aueb.cf.mobilecontacts.service;

import gr.aueb.cf.mobilecontacts.dao.IMobileContactDAO;
import gr.aueb.cf.mobilecontacts.dto.MobileContactInsertDTO;
import gr.aueb.cf.mobilecontacts.dto.MobileContactUpdateDTO;
import gr.aueb.cf.mobilecontacts.exceptions.ContactNotFoundException;
import gr.aueb.cf.mobilecontacts.exceptions.PhoneNumberAlreadyExistsException;
import gr.aueb.cf.mobilecontacts.mapper.Mapper;
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
            mobileContact = Mapper.mapInsertDTOToContact(dto);

            System.err.printf("MobileContactServiceImp Logger: %s was insert\n", mobileContact);
            return dao.insert(mobileContact);
        } catch (PhoneNumberAlreadyExistsException e) {
            System.err.printf("MobileContactServiceImp Logger: contact with phone number: %s already exist\n", dto.getPhoneNumber());
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
                throw new PhoneNumberAlreadyExistsException("Contact with phone number: " + dto.getPhoneNumber() + " already exists and can not be updated");
            }

            newContact = Mapper.mapUpdateDTOToContact(dto);
            System.err.printf("MobileContactServiceImp Logger: %s was updated with new info: %s\n ", mobileContact, newContact);
            return dao.update(dto.getId(), newContact);
        } catch (ContactNotFoundException | PhoneNumberAlreadyExistsException e) {
            System.err.printf("MobileContactServiceImp Logger: %s\n", e.getMessage());
            throw e;
        }

    }

    @Override
    public void deleteContactById(Long id) throws ContactNotFoundException {
        try {
            if (!dao.userIdExist(id)) {
                throw new ContactNotFoundException("Contact with id: " + id + " not found for delete.");
            }

            System.err.printf("MobileContactServiceImp Logger: contact with id: %d was deleted.\n", id);
            dao.deleteById(id);
        } catch (ContactNotFoundException e) {
            System.err.printf("MobileContactServiceImp Logger: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public MobileContact getContactById(Long id) throws ContactNotFoundException {
        MobileContact mobileContact;

        try {
            mobileContact = dao.getById(id);
            if (mobileContact == null) {
                throw new ContactNotFoundException("Contact with id: " + id + "not found");
            }
            return mobileContact;
        } catch (ContactNotFoundException e) {
            System.err.printf("Contact with id: %d was not found to get returned\n", id);
            throw e;
        }
    }

    @Override
    public List<MobileContact> getAllContacts() {
        return dao.getAll();
    }

    @Override
    public MobileContact getContactByPhoneNumber(String phoneNumber) throws ContactNotFoundException {
        MobileContact mobileContact;

        try {
            mobileContact = dao.getByPhoneNumber(phoneNumber);
            if (mobileContact == null) {
                throw new ContactNotFoundException("Contact with phone number: " + phoneNumber + " not found");
            }
            return mobileContact;
        } catch (ContactNotFoundException e) {
            System.err.printf("Contact with phone number: %s was not found to get returned\n", phoneNumber);
            throw e;
        }
    }

    @Override
    public void deleteContactByPhoneNumber(String phoneNumber) throws ContactNotFoundException {
        try {
            if (!dao.phoneNumberExist(phoneNumber)) {
                throw new ContactNotFoundException("Contact with phone number: " + phoneNumber + " not found for delete.");
            }

            System.err.printf("MobileContactServiceImp Logger: contact with phone number: %s was deleted.\n", phoneNumber);
            dao.deleteByPhoneNumber(phoneNumber);
        } catch (ContactNotFoundException e) {
            System.err.printf("MobileContactServiceImp Logger: %s\n", e.getMessage());
            throw e;
        }
    }

}
