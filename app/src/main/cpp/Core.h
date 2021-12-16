//
// Created by Onkar Ingale on 13-11-2021.
//

#ifndef CORE_H
#define CORE_H

#include <utility>

using namespace matrix;
using namespace dataset;

namespace linearreg
{
    class LinearReg
    {
    private:
        Dataset ds;
        Matrix<long double> x_train, y_train;
        Matrix<long double> betas;
        Matrix<long double> _coef;
        long double y_mean = 0;
        long double _r2_score = 0;
        long double _intercept = 0;

        //Functions
        void fit();

        void Calculate_Mean();

        void r2();


    public:
        //Constructor
        explicit LinearReg(Dataset ds);

        //Functions
        long double predict(std::vector<long double> a);

        //Info
        Matrix<long double> beta();

        Matrix<long double> coef();

        const long double intercept() const;

        std::string r2_score() const;


    };

    void LinearReg::Calculate_Mean()
    {
        for (int i = 0; i < y_train.rows(); ++i)
        {
            y_mean += y_train[i][0];
        }
        y_mean = y_mean / y_train.rows();
    }

    void LinearReg::r2()
    {
        Calculate_Mean();
        long double a_ss_y_m = 0; //actual sum((y - y_mean)^2)
        long double e_ss_y_m = 0; //Estimated sum((y - y_mean)^2)
        for (int i = 0; i < y_train.rows(); ++i)
        {
            a_ss_y_m += pow(y_train[i][0] - y_mean, 2);
            std::vector<long double> a(x_train[i].begin() + 1, x_train[i].end());
            e_ss_y_m += pow((predict(a) - y_mean), 2);
        }
        _r2_score = e_ss_y_m / a_ss_y_m;
    }

    LinearReg:: LinearReg(Dataset ds) : ds(std::move(ds))
    {
        fit();
        r2();
    }

    long double LinearReg::predict(std::vector<long double> a)
    {
        Matrix<long double> z(1, _coef.rows());
        for (int i = 0; i < z.cols(); ++i)
        {
            z[0][i] = a[i];
        }
        z = z * _coef;
        z[0][0] += _intercept;
        return z[0][0];
    }

    Matrix<long double> LinearReg::beta()
    {
        return betas;
    }

    const long double LinearReg::intercept() const
    {
        return _intercept;
    }

    Matrix<long double> LinearReg::coef()
    {
        return _coef;
    }

    void LinearReg::fit()
    {
        x_train.resize(ds.x_train.size() , ds.x_train[0].size()+1);
        y_train.resize(1, ds.y_train.size());

//        x_train = x_train.transpose();
        //Inserting 1 at beginning of Matrix
        for (int i = 0; i < x_train.rows(); ++i)
        {
            x_train[i][0] = 1;
        }
        //Converting Dataset into Matrix
        for (int i = 0; i < x_train.rows(); ++i)
        {
            for (int j = 1; j < x_train.cols(); ++j)
            {
                x_train[i][j] = ds.x_train[i ][j- 1];
            }
        }
        for (int i = 0; i < y_train.cols(); ++i)
        {
            y_train[0][i] = ds.y_train[i];
        }
        //Transposing Matrix Data because the input was done using Column Major Order
//        x_train = x_train.transpose();
        y_train = y_train.transpose();

        //Calculate Coeff
        betas = (((x_train.transpose() * x_train) ^ -1) * x_train.transpose()) * y_train;
//      std::cout<<"\n X_train : "<<x_train.dim()<<std::endl<<x_train;
//        std::cout<<"\n Y_train : "<<y_train.dim()<<std::endl<<y_train;
//        std::cout<<"\n Betas : "<<betas.dim();
//        std::cout<<betas;

        _coef.resize(betas.rows() - 1, 1);

        for (int i = 1; i < betas.rows(); ++i)
        {
            _coef[i - 1][0] = betas[i][0];
        }
        _intercept = betas[0][0];
    }

    std::string LinearReg::r2_score() const
    {
        return std::to_string(_r2_score);
    }

}

#endif //CORE_H
