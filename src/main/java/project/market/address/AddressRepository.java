package project.market.address;

import org.springframework.data.jpa.repository.JpaRepository;
import project.market.address.entity.Address;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByIdAndMemberId(Long addressId, Long memberId);

    List<Address> findAllByMemberId(Long memberId);

    long countByMemberId(Long memberId);

    Optional<Address> findFirstByMemberIdOrderByIdDesc(Long memberId);
}
