package fr.devsylone.fallenkingdom.utils;

import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Un arbre binaire de recherche (Binary Search Tree) d'éléments indexés dans
 * l'ordre de leur distance avec un point.
 *
 * <p>Les nœud du sous-arbre gauche ont une distance strictement inférieure à
 * celle du nœud au dessus, ceux à droite ont une distance strictement
 * supérieure.</p>
 *
 * @param <T> Élement localisable
 */
public class DistanceTree<T> implements Iterable<T> {

    private final Location origin;
    private @Nullable Node<T> root;

    public DistanceTree(@NotNull Location origin) {
        this.origin = requireNonNull(origin, "origin");
    }

    /**
     * Ajoute un élément localisable à l'arbre.
     *
     * <p>Les valeurs en doubles ou très voisines ne sont pas ajoutées.</p>
     *
     * @param location La position de l'élément.
     * @param located L'élément
     * @return S'il a été ajouté à l'arbre.
     */
    public boolean add(@NotNull Location location, @NotNull T located) {
        final int distance = (int) this.origin.distanceSquared(location); // Pas besoin de la précision du double
        if (this.root == null) {
            // L'arbre est vide, ce sera forcément la racine
            this.root = new Node<>(distance, located);
            return true;
        }

        Node<T> node = this.root;
        int comparison;
        while ((comparison = Integer.compare(distance, node.key)) != 0) {
            switch (comparison) {
                case -1:
                    if (node.left == null) {
                        // Pas de nœud à gauche (partie avec des valeurs inférieures) et c'est de ce côté que l'on souhaite ajouter
                        node.left = new Node<>(distance, located);
                        return true;
                    }
                    // Il y a une suite, on continue
                    node = node.left;
                    break;
                case 1:
                    if (node.right == null) {
                        // Pas de nœud à droite (partie avec des valeurs supérieures) et c'est de ce côté que l'on souhaite ajouter
                        node.right = new Node<>(distance, located);
                        return true;
                    }
                    // Il y a une suite, on continue
                    node = node.right;
                    break;
            }
        }

        // Doublon ou valeurs voisines
        return false;
    }

    /**
     * Cherche le n-ième élément de l'arbre selon la distance.
     *
     * <p>L'opération n'a pas une complexité constante, il est préférable
     * d'utiliser {@link #toList()} pour de nombreux accès par index.</p>
     *
     * @param index L'index à chercher.
     * @return L'élément à cet index, s'il existe.
     */
    public @NotNull Optional<@NotNull T> find(int index) {
        if (index < 0 || this.root == null) {
            return Optional.empty();
        }
        return this.root.get(index, new MutableInt(-1));
    }

    /**
     * Teste si l'arbre est vide.
     *
     * @return {@code true} s'il n'y a aucune valeur.
     */
    public boolean isEmpty() {
        return this.root == null;
    }

    /**
     * Détermine la taille de l'arbre.
     *
     * @return Le nombre de nœuds.
     */
    public int size() {
        if (this.root == null) {
            return 0;
        }
        return this.root.size();
    }

    /**
     * Détermine l'élément le plus proche de l'origine.
     *
     * @return Le plus proche, s'il y en a un.
     */
    public @Nullable T nearest() {
        if (this.root == null) {
            return null;
        }
        Node<T> node = this.root;
        while (node.left != null) {
            node = node.left;
        }
        return node.value;
    }

    /**
     * Détermine l'élément le plus loin de l'origine.
     *
     * @return Le plus loin, s'il y en a un.
     */
    public @Nullable T farthest() {
        if (this.root == null) {
            return null;
        }
        Node<T> node = this.root;
        while (node.right != null) {
            node = node.right;
        }
        return node.value;
    }

    /**
     * Transforme l'arbre en liste avec un parcours infixe (gauche, valeur, côté
     * droit).
     *
     * @return Une nouvelle liste mutable.
     */
    public @NotNull List<T> toList() {
        if (this.root == null) {
            return new ArrayList<>(0);
        }
        final List<T> list = new ArrayList<>();
        this.root.visitInfix(list);
        return list;
    }

    @Override
    public @NotNull java.util.Iterator<T> iterator() {
        if (this.root == null) {
            return Collections.emptyIterator();
        }
        return this.toList().iterator();
    }

    static class Node<T> {
        final int key;
        final T value;
        @Nullable Node<T> left, right;

        Node(int key, T value) {
            this.key = key;
            this.value = value;
        }

        int size() {
            int l = 0, r = 0;
            if (this.left != null) {
                l = this.left.size();
            }
            if (this.right != null) {
                r = this.right.size();
            }
            return 1 + l + r;
        }

        void visitInfix(@NotNull List<T> list) {
            if (this.left != null) {
                this.left.visitInfix(list);
            }
            list.add(this.value);
            if (this.right != null) {
                this.right.visitInfix(list);
            }
        }

        @NotNull Optional<@NotNull T> get(int index, @NotNull MutableInt current) {
            Optional<T> value;
            if (this.left != null) {
                value = this.left.get(index, current);
                if (value.isPresent()) {
                    return value;
                }
            }
            current.increment();
            if (index == current.intValue()) {
                return Optional.of(this.value);
            }
            if (this.right != null) {
                value = this.right.get(index, current);
                return value;
            }
            return Optional.empty();
        }
    }
}
