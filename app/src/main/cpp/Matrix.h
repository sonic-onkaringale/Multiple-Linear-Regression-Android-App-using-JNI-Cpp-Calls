//
// Created by Onkar Ingale on 06-11-2021.
//

#ifndef MATRIX_H // Directive Guards
#define MATRIX_H

#include <vector>
#include <stdexcept>
#include <iomanip>
#include <cmath>
#include "Eigen/Dense" // Library Eigen
//#include "NumCpp.hpp" // Library Deprecated Due to Required Build System install

namespace matrix
{
    template<class T=long double>
    class Matrix
    {
    private:
        std::vector<std::vector<T>> self;

        static size_t number_of_digits(long double n);
#ifdef LINEARREG_H
        friend class linearreg::LinearReg;
#endif


    public:
        //Constructors
        Matrix() = default;

        Matrix(size_t rows, size_t cols);

        Matrix(size_t rows, size_t cols, T fill); //Delegating Constructor

        //Infos
        size_t size() const;

        size_t rows() const;

        size_t cols() const;

        std::string dim();

        // Functions
        Matrix transpose();

        Matrix inverse();

        void resize(size_t rows, size_t cols);

        void resize(size_t rows, size_t cols, double fill);


        //Operators
        std::vector<T> &operator[](int i)
        {
            return static_cast<std::vector<T> &>(self[i]);
        }

        std::vector<T> &operator[](int i) const
        {
            return const_cast<std::vector<T> &>(self[i]);
        }

        friend Matrix operator*(Matrix<T> const &x, Matrix<T> const &y)
        {
            if (x.cols() != y.rows())
            {
                if (x.rows() == y.cols())
                {
                    throw std::invalid_argument("\n1. Multiplication Not Compatible,"
                                                " may be your order of expression is wrong\n");
                } else throw std::invalid_argument("\n2 .Multiplication Not Compatible\n");
            }
            Matrix<T> z(x.rows(), y.cols());

            for (int i = 0; i < x.rows(); ++i)
            {
                for (int j = 0; j < y.cols(); ++j)
                {
                    for (int k = 0; k < x.cols(); ++k)
                    {
                        z.self[i][j] += x.self[i][k] * y.self[k][j];
                    }
                }
            }

            return z;
        }

        friend Matrix operator^(Matrix<T> const &x, int const &y)
        {
            Matrix<T> z(x.rows(), x.cols());
            if (y == 0 || y < -1)
            {
                throw std::out_of_range("\n Invalid Use of ^ operator \n ");
            } else if (y == -1)
            {
                Eigen::MatrixXd e_mat(x.rows(), x.cols());
                for (int i = 0; i < x.rows(); ++i)
                {
                    for (int j = 0; j < x.cols(); ++j)
                    {
                        e_mat(i, j) = x[i][j];
                    }
                }
                e_mat = e_mat.inverse();
                for (int i = 0; i < x.rows(); ++i)
                {
                    for (int j = 0; j < x.cols(); ++j)
                    {
                        z[i][j] = e_mat(i, j);
                    }
                }
            } else if (y == 1)
            {
                return x;
            } else
            {
                z = x * x;
                for (int i = 2; i < y; ++i)
                {
                    z = z * x;
                }
            }

            return z;
        }

        Matrix &operator=(Matrix<T> const &y)
        {
            this->resize(y.rows(), y.cols());
            this->self = y.self;
            return *this;
        }

        //Input and Output Streams
        friend std::ostream &operator<<(std::ostream &out, Matrix<T> M)
        {
            size_t n = M.rows();
            size_t m = M.cols();
            size_t max_len_per_column[M.cols()];

            for (size_t j = 0; j < m; ++j)
            {
                size_t max_len{};

                for (size_t i = 0; i < n; ++i)
                {
                    const auto num_length
                            {
                                    number_of_digits(M[i][j])
                            };
                    if (num_length > max_len)
                        max_len = num_length;
                }

                max_len_per_column[j] = max_len;
            }

            for (size_t i = 0; i < n; ++i)
                for (size_t j = 0; j < m; ++j)
                    out << std::setprecision(10) << (j == 0 ? "\n| " : "") << std::setw(max_len_per_column[j])
                        << M[i][j]
                        << (j == m - 1 ? " |" : " ");

            out << '\n';
            return out;
        }

        friend std::istream &operator>>(std::istream &in, Matrix<T> &M)
        {
            for (int i = 0; i < M.rows(); ++i)
            {
                for (int j = 0; j < M.cols(); ++j)
                {
                    in >> M[i][j];
                }
            }
            return in;
        }

    };

    template<class T>
    size_t Matrix<T>::number_of_digits(long double n)
    {
        std::ostringstream strs;

        strs << n;
        return strs.str().size();
    }

    template<class T>
    Matrix<T>::Matrix(size_t rows, size_t cols)
    {
        if (rows < 0 || cols < 0)
        {
            throw std::out_of_range("\n Invalid Dimension of Matrix \n ");
        }
        if (rows == 0 && cols == 0)
            return;
        self.resize(rows);
        for (int i = 0; i < self.size(); ++i)
            self[i].resize(cols);

        for (int i = 0; i < self.size(); ++i)
        {
            for (int j = 0; j < self[0].size(); ++j)
            {
                self[i][j] = 0;
            }
        }

    }

    template<class T>
    Matrix<T>::Matrix(size_t rows, size_t cols, T fill) : Matrix(rows, cols)
    {
        for (int i = 0; i < this->rows(); ++i)
        {
            for (int j = 0; j < this->rows(); ++j)
            {
                self[i][j] = fill;
            }
        }
    }

    template<class T>
    size_t Matrix<T>::size() const
    {
        return self.size();
    }

    template<class T>
    size_t Matrix<T>::rows() const
    {
        return self.size();
    }

    template<class T>
    size_t Matrix<T>::cols() const
    {
        return self[0].size();
    }

    template<class T>
    std::string Matrix<T>::dim()
    {
        return (std::to_string(self.size()) + " x " + std::to_string(self[0].size()));
    }

    template<class T>
    Matrix<T> Matrix<T>::transpose()
    {
        Matrix<T> z(this->cols(), this->rows());
        for (int i = 0; i < this->rows(); ++i)
        {
            for (int j = 0; j < this->cols(); ++j)
            {
                z[j][i] = this->self[i][j];
            }
        }
        return z;
    }

    template<class T>
    Matrix<T> Matrix<T>::inverse()
    {
        return operator^(*this, -1);
    }

    template<class T>
    void Matrix<T>::resize(size_t rows, size_t cols)
    {
        self.resize(rows);
        for (int i = 0; i < self.size(); ++i)
        {
            self[i].resize(cols);
        }
    }

    template<class T>
    void Matrix<T>::resize(size_t rows, size_t cols, double fill)
    {
        resize(rows, cols);
        for (int i = 0; i < this->rows(); ++i)
        {
            for (int j = 0; j < this->cols(); ++j)
            {
                self[i][j] = fill;
            }
        }
    }

}
#endif //MATRIX_H
