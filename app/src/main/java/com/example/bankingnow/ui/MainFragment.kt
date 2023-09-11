package com.example.bankingnow.ui

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.fragment.findNavController
import com.example.bankingnow.R
import com.example.bankingnow.databinding.FragmentMainBinding
import com.example.bankingnow.databinding.FragmentRemitBinding
import com.example.writenow.base.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {
    override fun initStartView() {
        super.initStartView()
    }

    override fun initDataBinding() {
        super.initDataBinding()


    }


    override fun initAfterBinding() {
        super.initAfterBinding()

        binding.btnBalance.setOnClickListener{
            navController.navigate(R.id.action_mainFragment_to_balanceFragment)
        }
        binding.btnRemit.setOnClickListener{
            navController.navigate(R.id.action_mainFragment_to_remitFragment)
        }
    }
}