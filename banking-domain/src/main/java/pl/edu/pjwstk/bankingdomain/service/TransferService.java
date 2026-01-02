package pl.edu.pjwstk.bankingdomain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.bankingcommon.CustomException.TransferException;
import pl.edu.pjwstk.bankingdomain.model.Account;
import pl.edu.pjwstk.bankingdomain.model.Transfer;
import pl.edu.pjwstk.bankingdomain.repository.AccountRepository;
import pl.edu.pjwstk.bankingdomain.repository.TransferRepository;
import pl.edu.pjwstk.bankingservice.dto.TransferDto;
import pl.edu.pjwstk.bankingservice.dto.TransferRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;


    public List<TransferDto> getAll(){
        return transferRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public TransferDto getTransferById(Long id) throws TransferException {
        return transferRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new TransferException("Transfer not found"));
    }

    @Transactional
    public void executeTransfer(TransferRequest request) throws TransferException {
        Account sender = accountRepository.findAccountByIban(request.getSenderIban())
                .orElseThrow(() -> new TransferException("Cannot find sender account"));

        Account receiver = accountRepository.findAccountByIban(request.getReceiverIban())
                .orElseThrow(() -> new TransferException("Cannot find receiver account"));

        if(sender.equals(receiver)){
            throw new TransferException("Cannot transfer to yourself you fucking idiot");
        }

        if(sender.getBalance().compareTo(request.getAmountSent()) < 0){
            throw new TransferException("Insufficient funds");
        }


        BigDecimal rate = calculateRate(sender, receiver);
        BigDecimal amountReceived = request.getAmountSent().multiply(rate).setScale(2, RoundingMode.FLOOR);

        sender.setBalance(sender.getBalance().subtract(request.getAmountSent()));
        receiver.setBalance(receiver.getBalance().add(amountReceived));

        Transfer transfer = new Transfer();
        transfer.setSenderIban(sender);
        transfer.setReceiverIban(receiver);
        transfer.setAmountSent(request.getAmountSent());
        transfer.setAmountReceived(amountReceived);
        transfer.setExchangeRate(rate);

        transferRepository.save(transfer);
        accountRepository.save(sender);
        accountRepository.save(receiver);

    }

    public BigDecimal calculateRate(Account sender, Account receiver){

        if(sender.getCurrency().getCode().equals(receiver.getCurrency().getCode())){
            return BigDecimal.ONE;
        }

        return sender.getCurrency().getExchangeRate().divide(receiver.getCurrency().getExchangeRate(), 5, RoundingMode.FLOOR);
    }

    public TransferDto convertToDto(Transfer transfer){
        return new TransferDto(
                transfer.getId(),
                transfer.getSenderIban().getIban(),
                transfer.getReceiverIban().getIban(),
                transfer.getAmountSent(),
                transfer.getAmountReceived(),
                transfer.getExchangeRate(),
                transfer.getCreatedAt()
        );
    }
}
