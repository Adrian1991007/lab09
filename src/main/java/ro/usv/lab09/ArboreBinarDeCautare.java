package ro.usv.lab09;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public class ArboreBinarDeCautare<E extends Comparable<? super E>> {
    static class Nod<E> {
        private E info;
        private Nod<E> stg;
        private Nod<E> dr;

        Nod(E info) {
            this.info = info;
            stg = null;
            dr = null;
        }

        private boolean esteFrunza() {
            return stg == null && dr == null;
        }

        @Override
        public String toString() {
            return info.toString();
        }
    }

    private Nod<E> radacina = null;
    private final ArrayList<Nod<E>> noduri = new ArrayList<>();

    public boolean isEmpty() {
        return radacina == null;
    }

    // ================================================
// inserarea unor chei in arbore
// ================================================
    public boolean add(E val) {
        if (isEmpty()) {
            radacina = new Nod<>(val);
            return true;
        } else
            return addRecursiv(radacina, val);
    }

    //
// daca E nu este Comparable se lanseaza exceptia ClassCastException
//
    public boolean addRecursiv(Nod<E> nodcrt, E val) {
        if (Objects.equals(nodcrt.info, val)) {
            return false;
        }
        if (val.compareTo(nodcrt.info) < 0) {
// val este mai mica decat ce se gaseste in nodcrt
            if (nodcrt.stg != null) {
                return addRecursiv(nodcrt.stg, val);
            } else {
                nodcrt.stg = new Nod<>(val);
            }
        } else {
// val este mai mare decat ce se gaseste in nodcrt
            if (nodcrt.dr != null)
                return addRecursiv(nodcrt.dr, val);
            else
                nodcrt.dr = new Nod<>(val);
        }
        return true;
    }

    // ================================================
// cautarea unei chei in arbore
// ================================================
    public boolean contains(E val) {
        return !isEmpty() && containsRecursiv(radacina, val);
    }

    private boolean containsRecursiv(Nod<E> nodcrt, E val) {
        if (nodcrt == null)
            return false;
        if (Objects.equals(nodcrt.info, val))
            return true;
        if (val.compareTo(nodcrt.info) < 0) {
// se cauta in subarborele stang
            return containsRecursiv(nodcrt.stg, val);
        } else {
// se cauta in sbarborele drept
            return containsRecursiv(nodcrt.dr, val);
        }
    }

    // =========================================================
// tarversari ale arborelui (preordine-RSD si inordine-SRD)
// =========================================================
    public ArrayList<Nod<E>> SRD() {
        noduri.clear();
        SRDrec(radacina);
        return noduri;
    }

    void SRDrec(Nod<E> x) {
        if (x == null) return;
        SRDrec(x.stg); //S...
        noduri.add(x); //R
        //System.out.print(x + " "); //R
        SRDrec(x.dr); //D
    }

    public ArrayList<Nod<E>> RSD() {
        noduri.clear();
        RSDrec(radacina);
        return noduri;
    }

    void RSDrec(Nod<E> x) {
        if (x == null) return;
        noduri.add(x); //R
        RSDrec(x.stg); //S...
        RSDrec(x.dr); //D
    }

    public ArrayList<Nod<E>> SDR() {
        noduri.clear();
        SDRrec(radacina);
        return noduri;
    }

    void SDRrec(Nod<E> x) {
        if (x == null) return;
        SDRrec(x.stg); //S...
        SDRrec(x.dr); //D
        noduri.add(x); //R
    }

    // ==================================================
// returneaza cea mai mica cheie din arbore (prima)
// ==================================================
    public E first() {
        return isEmpty() ? null : firstRecursiv(radacina);
    }

    private E firstRecursiv(ArboreBinarDeCautare.Nod<E> nodcrt) {
        return nodcrt.stg == null ? nodcrt.info :
                firstRecursiv(nodcrt.stg);
    }

    // ==================================================
// elimina din arbore nodul cu informatia val
// (varianta recursiva - removeRec)
// ==================================================
    private boolean gasit;

    public boolean removeRec(E val) {
        gasit = false;
// este posibil ca radacina sa fie eliminata
        radacina = removeRecursiv(radacina, val);
        return gasit;
    }

    private ArboreBinarDeCautare.Nod<E> removeRecursiv(
            ArboreBinarDeCautare.Nod<E> nodcrt, E val) {
        if (nodcrt == null) {
            return null;
        }
        if (!Objects.equals(nodcrt.info, val)) {
// val nu este in nodul curent (nodcrt)
            if (val.compareTo(nodcrt.info) < 0) {
// nodul de eliminat poate fi in subarb. stang
                nodcrt.stg = removeRecursiv(nodcrt.stg, val);
            } else {
// nodul de eliminat poate fi in subarb. drept
                nodcrt.dr = removeRecursiv(nodcrt.dr, val);
            }
        } else { // info == val
// s-a gasit nodul ce trebuie eliminat
            gasit = true;
            if (nodcrt.stg == null && nodcrt.dr == null) {
// Cazul 1) este un nod frunza => va fi eliminat
                return null;
            }
// nu este frunza! Are 1 sau 2 fii
            if (nodcrt.dr == null) {
// Cazul 2') are un singur fiu, cel din stanga
// acesta ii va lua locul
                return nodcrt.stg;
            }
            if (nodcrt.stg == null) {
// Cazul 2") are un singur fiu, cel din dreapta
// acesta ii va lua locul
                return nodcrt.dr;
            }
//
// Cazul 3) are 2 fii; va fi eliminat cel mai mic nod care
// este in acelasi timp mai mare decat val
//
            E valMin = firstRecursiv(nodcrt.dr);
            nodcrt.info = valMin; // valMin > val deoarece s-a cautat la dr.
// valMin se pune in nodul eliminat
// apoi se testRemove nodul cu valMin
            nodcrt.dr = removeRecursiv(nodcrt.dr, valMin);
        }
        return nodcrt;
    }

    public int h_arbore() {
        return h_arboreRec(radacina);
    }

    public int h_arboreRec(Nod<E> nod) {
        if (nod == null)
            return 0;
        return 1 + Math.max(h_arboreRec(nod.stg), h_arboreRec(nod.dr));
    }

    // ================================================================
// verificarea metodelor remove(), contains(), first() si RSD()
// ================================================================
    private void testRemove(E el) {
        testRemove(el, this::removeRec);
    }

    //
// Al III-lea argument este prevazut pt. a testa si remove iterativ
//
    private void testRemove(E el, Function<E, Boolean> fct) {
        if (!fct.apply(el)) {
            System.out.println("remove: " + el + " nu s-a gasit in arbore");
        } else {
            if (contains(el))
                System.out.println("*** Eroare: remove(" + el + ") nereusit!");
            else
                System.out.print("Executat remove ( " + el + " )\nSRD: ");
            SRD();
        }
        System.out.print("RSD: ");
        RSD();
    }

    private static ArboreBinarDeCautare<Integer> initArbore(int[] chei) {
        ArboreBinarDeCautare<Integer> arb = new ArboreBinarDeCautare<>();
        for (int x : chei)
            arb.add(x);
        System.out.print("initArbore SRD: ");
        arb.SRD();
        return arb;
    }

    private static void removeTestChei(int[] chei, boolean initArbRepetat) {
        System.out.println("\n----- TEST REMOVE RECURSIV " +
                (initArbRepetat ? "cu" : "fara") +
                " initializare arbore dupa fiecare remove ------");
        ArboreBinarDeCautare<Integer> tst = initArbore(chei);
        for (int x : chei) {
            System.out.println("se incearca remove (" + x + ")");
            tst.testRemove(x);
            if (initArbRepetat)
                tst = initArbore(chei);
        }
    }
}