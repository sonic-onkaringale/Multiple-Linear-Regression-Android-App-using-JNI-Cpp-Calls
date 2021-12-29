//
// Created by Onkar Ingale on 12-11-2021.
//

#ifndef DATASET_H
#define DATASET_H


#include <vector>


namespace dataset
{
    class Dataset
    {
    private:
//        std::string filepath;
        std::vector<std::vector<long double>> x_train;
        std::vector<long double> y_train;
        std::vector<std::string> x_header;
        std::string y_header;

        friend class linearreg::LinearReg;


    public:
        //Constructor
        Dataset(std::vector<std::string> headers,std::vector<std::vector<long double>> ds)
        {
            //Setting Headers
            x_header.resize(headers.size()-1);
            for (int i = 0; i < x_header.size(); ++i)
            {
                x_header[i] = headers[i];
            }
            y_header = headers[headers.size()-1];

            //Setting Dataset
            x_train.resize(ds.size());
            for (int i = 0; i < x_train.size(); ++i)
            {
                x_train[i].resize(ds[i].size() -1);
                for (int j = 0; j < x_train[i].size(); ++j)
                {
                    x_train[i][j] = ds[i][j];
                }
            }

            y_train.resize(ds.size());
            for (int i = 0; i < y_train.size(); ++i)
            {
                y_train[i] = ds[i][ds[i].size()-1];
            }
        }
        std::string shape();
    };


    std::string Dataset::shape()
    {
        return (std::to_string(x_train[0].size()) + " x " + std::to_string(x_header.size() + 1));
    }

}
#endif //DATASET_H
