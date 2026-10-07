package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    /**
     * Rules for an employee {@code e} to be able to work a shift, shared by the candidates list and the swap validation.
     * An employee is eligible when they are ACTIVE and:
     * <ul>
     *     <li>have no other non-canceled shift overlapping the shift time on that date;</li>
     *     <li>have no APPROVED time off covering the shift date;</li>
     *     <li>have no "not available" block overlapping the shift time on that weekday;</li>
     *     <li>if they declared "available" windows for that weekday, one of them covers the whole shift
     *     (no availability declared for the weekday means no restriction).</li>
     * </ul>
     */
    String SHIFT_ELIGIBILITY = """
            e.status = com.ficsolution.roster.model.enums.EmployeeStatus.ACTIVE
            and not exists (
                select 1 from Shift ss
                where ss.employee = e
                and ss.shiftDate = :shiftDate
                and ss.status <> com.ficsolution.roster.model.enums.ShiftStatus.CANCELED
                and ss.startTime < :endTime
                and ss.endTime > :startTime
            )
            and not exists (
                select 1 from TimeOffRequest t
                where t.employee = e
                and t.status = com.ficsolution.roster.model.enums.RequestStatus.APPROVED
                and t.startDate <= :shiftDate
                and t.endDate >= :shiftDate
            )
            and not exists (
                select 1 from Availability na
                where na.employee = e
                and na.weekday = :weekday
                and na.isAvailable = false
                and na.startTime < :endTime
                and na.endTime > :startTime
            )
            and (
                not exists (
                    select 1 from Availability da
                    where da.employee = e
                    and da.weekday = :weekday
                    and da.isAvailable = true
                )
                or exists (
                    select 1 from Availability a
                    where a.employee = e
                    and a.weekday = :weekday
                    and a.isAvailable = true
                    and a.startTime <= :startTime
                    and a.endTime >= :endTime
                )
            )
            """;

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"role", "role.permissions"})
    Optional<Employee> findByEmail(String email);

    @EntityGraph(attributePaths = "role")
    Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);

    @Query("select e from Employee e where e.id <> :ownerId and " + SHIFT_ELIGIBILITY + " order by e.name")
    List<Employee> findEligibleForShift(UUID ownerId, LocalDate shiftDate, DayOfWeek weekday,
                                        LocalTime startTime, LocalTime endTime);

    @Query("select case when count(e) > 0 then true else false end from Employee e where e.id = :employeeId and "
            + SHIFT_ELIGIBILITY)
    boolean isEligibleForShift(UUID employeeId, LocalDate shiftDate, DayOfWeek weekday,
                               LocalTime startTime, LocalTime endTime);
}
